param(
    [string[]]$Commands = @(),
    [string[]]$ExpectedOutputs = @(),
    [string]$PlanFile = (Join-Path $PSScriptRoot 'ui-test-plan.md')
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$srcDir = Join-Path $projectRoot 'src\main\java'
$outDir = Join-Path $projectRoot 'out'
$divider = '____________________________________________________________'
$welcome = "$divider`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nGood day. I am MoistBot, at your service.`nHow may I assist you today?`n$divider"
$exitMessage = 'Thank you for using MoistBot. Have a pleasant day.'
$emptyListMessage = 'Your task list is presently empty. You may use: bye, list, todo, deadline, event, mark, or unmark.'

function Normalize-Output([string]$value) {
    return ($value -replace "`r`n", "`n" -replace "`r", "`n").TrimEnd()
}

function Format-Session([string[]]$messages) {
    return "$welcome`n$divider`n" + ($messages -join "`n$divider`n$divider`n") + "`n$divider"
}

function Run-Case([string]$name, [string[]]$caseCommands, [string]$expected) {
    Write-Host "=== Running $name ==="
    Write-Host 'Input:'
    $caseCommands | ForEach-Object { Write-Host $_ }

    if (-not (Test-Path $outDir)) {
        New-Item -ItemType Directory -Path $outDir -Force | Out-Null
    }

    $javaFiles = Get-ChildItem -Path $srcDir -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName
    javac -d $outDir $javaFiles *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed while building MoistBot before running $name."
    }

    $actual = @($caseCommands) | java -cp $outDir moistbot.MoistBot 2>&1 | Out-String
    $actualText = Normalize-Output $actual
    $expectedText = Normalize-Output $expected

    Write-Host ''
    Write-Host 'Output:'
    Write-Host $actualText
    Write-Host ''

    if ($actualText -ne $expectedText) {
        Write-Host "FAILED: $name" -ForegroundColor Red
        Write-Host 'Expected output:' -ForegroundColor Yellow
        Write-Host $expectedText
        Write-Host ''
        Write-Host 'Actual output:' -ForegroundColor Yellow
        Write-Host $actualText
        exit 1
    }

    Write-Host "PASS: $name" -ForegroundColor Green
    Write-Host ''
}

if ($Commands.Count -gt 0 -or $ExpectedOutputs.Count -gt 0) {
    if ($Commands.Count -ne $ExpectedOutputs.Count) {
        throw 'The number of command lists and expected output blocks must match.'
    }
    Run-Case -name 'Provided case' -caseCommands $Commands -expected ($ExpectedOutputs -join "`n")
    exit
}

$cases = @(
    @{ Name = 'startup and exit'; Commands = @('bye'); Messages = @($exitMessage) },
    @{ Name = 'empty list'; Commands = @('list', 'bye'); Messages = @(
        "Certainly. Here is your task list:`n$emptyListMessage", $exitMessage) },
    @{ Name = 'add todo and list'; Commands = @('todo buy milk', 'list', 'bye'); Messages = @(
        "Certainly. I have added this task:`n[T][ ] buy milk`nYour list now contains 1 task.",
        "Certainly. Here is your task list:`n1.[T][ ] buy milk", $exitMessage) },
    @{ Name = 'add deadline, event, mark, and unmark'; Commands = @(
        'todo read book', 'deadline return book /by Friday', 'event team meeting /from 2pm /to 4pm',
        'mark 2', 'list', 'unmark 2', 'bye'); Messages = @(
        "Certainly. I have added this task:`n[T][ ] read book`nYour list now contains 1 task.",
        "Certainly. I have added this task:`n[D][ ] return book (by: Friday)`nYour list now contains 2 tasks.",
        "Certainly. I have added this task:`n[E][ ] team meeting (from: 2pm to: 4pm)`nYour list now contains 3 tasks.",
        "Certainly. I have marked this task as complete:`n[D][X] return book (by: Friday)",
        "Certainly. Here is your task list:`n1.[T][ ] read book`n2.[D][X] return book (by: Friday)`n3.[E][ ] team meeting (from: 2pm to: 4pm)",
        "Certainly. I have marked this task as incomplete:`n[D][ ] return book (by: Friday)", $exitMessage) },
    @{ Name = 'unrecognised command'; Commands = @('buy groceries today', 'list', 'bye'); Messages = @(
        "My apologies, but I do not recognise the command 'buy'. Available commands are: bye, list, todo, deadline, event, mark, and unmark.",
        "Certainly. Here is your task list:`n$emptyListMessage", $exitMessage) },
    @{ Name = 'malformed additions explain the required correction'; Commands = @(
        'todo', 'deadline', 'deadline pay bills', 'deadline /by Friday', 'deadline pay bills /by',
        'deadline pay bills /by Friday /by Saturday', 'event meeting /from 2pm', 'event meeting /to 4pm',
        'event /from 2pm /to 4pm', 'event meeting /from /to 4pm', 'event meeting /from 2pm /to',
        'event meeting /to 4pm /from 2pm', 'event meeting /from 1pm /from 2pm /to 4pm',
        'deadline revise notes /by Friday', 'event lab /from 10am /to 12pm', 'list', 'bye'); Messages = @(
        "Please provide a description for the todo task. Usage: todo <description>, for example 'todo buy milk'.",
        "Please provide a deadline description and time. Usage: deadline <desc> /by <time>, for example 'deadline return book /by Friday'.",
        "Please include the '/by' separator. Usage: deadline <desc> /by <time>.",
        "Please provide a deadline description before '/by'. Usage: deadline <desc> /by <time>.",
        "Please provide a deadline time after '/by'. Usage: deadline <desc> /by <time>.",
        "A deadline may contain only one '/by' separator. Usage: deadline <desc> /by <time>.",
        "Please include the '/to' separator. Usage: event <desc> /from <time> /to <time>.",
        "Please include the '/from' separator. Usage: event <desc> /from <time> /to <time>.",
        "Please provide an event description before '/from'. Usage: event <desc> /from <time> /to <time>.",
        "Please provide an event start time after '/from'. Usage: event <desc> /from <time> /to <time>.",
        "Please provide an event end time after '/to'. Usage: event <desc> /from <time> /to <time>.",
        "Please place '/from' before '/to'. Usage: event <desc> /from <time> /to <time>.",
        "An event must contain exactly one '/from' and one '/to' separator. Usage: event <desc> /from <time> /to <time>.",
        "Certainly. I have added this task:`n[D][ ] revise notes (by: Friday)`nYour list now contains 1 task.",
        "Certainly. I have added this task:`n[E][ ] lab (from: 10am to: 12pm)`nYour list now contains 2 tasks.",
        "Certainly. Here is your task list:`n1.[D][ ] revise notes (by: Friday)`n2.[E][ ] lab (from: 10am to: 12pm)", $exitMessage) },
    @{ Name = 'invalid mark and unmark preserve completion state'; Commands = @(
        'mark 1', 'todo read book', 'mark', 'unmark one', 'mark 2147483648', 'mark 0', 'mark 2',
        'mark 1 extra', 'mark 1', 'list', 'unmark 1', 'list', 'bye'); Messages = @(
        "My apologies, but I cannot mark a task because your task list is empty. Please add a task first, then use 'mark <task number>'.",
        "Certainly. I have added this task:`n[T][ ] read book`nYour list now contains 1 task.",
        "Please provide a task number. Usage: mark <task number>, for example 'mark 1'.",
        "My apologies, but 'one' is not a valid task number. Please enter one whole number, for example 'unmark 1'.",
        "My apologies, but '2147483648' is not a valid task number. Please enter one whole number, for example 'mark 1'.",
        "Please provide a task number of at least 1. Use 'list' to view the available task numbers.",
        "My apologies, but task 2 does not exist. Please choose a number from 1 to 1. Use 'list' to view the tasks.",
        "My apologies, but '1 extra' is not a valid task number. Please enter one whole number, for example 'mark 1'.",
        "Certainly. I have marked this task as complete:`n[T][X] read book",
        "Certainly. Here is your task list:`n1.[T][X] read book",
        "Certainly. I have marked this task as incomplete:`n[T][ ] read book",
        "Certainly. Here is your task list:`n1.[T][ ] read book", $exitMessage) },
    @{ Name = 'blank input is handled as a chatbot error'; Commands = @('', 'bye'); Messages = @(
        "Please enter a command, such as 'list' or 'todo buy milk'.", $exitMessage) },
    @{ Name = 'argument-free commands reject extra text'; Commands = @('list now', 'bye now', 'list', 'bye'); Messages = @(
        "The 'list' command does not accept arguments. Please enter only 'list'.",
        "The 'bye' command does not accept arguments. Please enter only 'bye'.",
        "Certainly. Here is your task list:`n$emptyListMessage", $exitMessage) }
)

$capacityCommands = @(1..100 | ForEach-Object { "todo task $_" }) + @('todo overflow task', 'list', 'bye')
$capacityMessages = @(1..100 | ForEach-Object {
    $taskNoun = if ($_ -eq 1) { 'task' } else { 'tasks' }
    "Certainly. I have added this task:`n[T][ ] task $_`nYour list now contains $_ $taskNoun."
})
$capacityMessages += 'My apologies, but your task list is full (maximum 100 tasks). No task has been added.'
$capacityList = @(1..100 | ForEach-Object { "$_.[T][ ] task $_" }) -join "`n"
$capacityMessages += "Certainly. Here is your task list:`n$capacityList"
$capacityMessages += $exitMessage
$cases += @{ Name = 'task capacity error preserves the list'; Commands = $capacityCommands; Messages = $capacityMessages }

Write-Host "Test plan: $PlanFile"
foreach ($case in $cases) {
    Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages)
}

Write-Host 'All UI checks passed.' -ForegroundColor Green
