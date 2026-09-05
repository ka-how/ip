param(
    [string[]]$Commands = @(),
    [string[]]$ExpectedOutputs = @(),
    [string]$PlanFile = (Join-Path $PSScriptRoot 'ui-test-plan.md')
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$srcDir = Join-Path $projectRoot 'src\main\java'
$outDir = Join-Path $projectRoot 'out'

function Normalize-Output([string]$value) {
    return ($value -replace "`r`n", "`n" -replace "`r", "`n").TrimEnd()
}

function Run-Case([string]$name, [string[]]$caseCommands, [string[]]$caseExpectedOutput) {
    $expected = ($caseExpectedOutput -join "`n")

    Write-Host "=== Running $name ==="
    Write-Host 'Input:'
    if ($caseCommands.Count -gt 0) {
        $caseCommands | ForEach-Object { Write-Host $_ }
    }

    if (-not (Test-Path $outDir)) {
        New-Item -ItemType Directory -Path $outDir -Force | Out-Null
    }

    $javaFiles = Get-ChildItem -Path $srcDir -Filter '*.java' | Select-Object -ExpandProperty FullName
    javac -d $outDir $javaFiles *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed while building MoistBot before running $name."
    }

    $actual = @($caseCommands) | java -cp $outDir MoistBot 2>&1 | Out-String
    $actualText = $actual.TrimEnd()
    $expectedText = Normalize-Output $expected
    $actualTextNormalized = Normalize-Output $actualText

    Write-Host ''
    Write-Host 'Output:'
    Write-Host $actualText
    Write-Host ''

    if ($actualTextNormalized -ne $expectedText) {
        Write-Host "FAILED: $name" -ForegroundColor Red
        Write-Host 'Expected output:' -ForegroundColor Yellow
        Write-Host $expectedText
        Write-Host ''
        Write-Host 'Actual output:' -ForegroundColor Yellow
        Write-Host $actualTextNormalized
        exit 1
    }

    Write-Host "PASS: $name" -ForegroundColor Green
    Write-Host ''
}

if ($Commands.Count -gt 0 -or $ExpectedOutputs.Count -gt 0) {
    if ($Commands.Count -ne $ExpectedOutputs.Count) {
        throw 'The number of command lists and expected output blocks must match.'
    }

    $cases = @(
        @{ Name = 'Provided case'; Commands = $Commands; ExpectedOutputs = $ExpectedOutputs }
    )
} else {
    $welcome = "____________________________________________________________`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nHello! I'm MoistBot`nHow can I help you today?`n____________________________________________________________"
    $divider = '____________________________________________________________'
    $exitMessage = "$divider`nBye! Have a nice day!`n$divider"
    $malformedAdditionCommands = @(
        'todo',
        'deadline',
        'deadline pay bills',
        'deadline /by Friday',
        'deadline pay bills /by',
        'deadline pay bills /by Friday /by Saturday',
        'event meeting /from 2pm',
        'event meeting /to 4pm',
        'event /from 2pm /to 4pm',
        'event meeting /from /to 4pm',
        'event meeting /from 2pm /to',
        'event meeting /to 4pm /from 2pm',
        'event meeting /from 1pm /from 2pm /to 4pm',
        'deadline revise notes /by Friday',
        'event lab /from 10am /to 12pm',
        'list',
        'bye'
    )
    $malformedAdditionMessages = @(
        "Todo description missing. Usage: todo <description>, for example 'todo buy milk'.",
        "Deadline description and time missing. Usage: deadline <desc> /by <time>, for example 'deadline return book /by Friday'.",
        "Deadline separator '/by' missing. Usage: deadline <desc> /by <time>.",
        "Deadline description missing before '/by'. Usage: deadline <desc> /by <time>.",
        "Deadline time missing after '/by'. Usage: deadline <desc> /by <time>.",
        "A deadline can contain only one '/by' separator. Usage: deadline <desc> /by <time>.",
        "Event end separator '/to' missing. Usage: event <desc> /from <time> /to <time>.",
        "Event start separator '/from' missing. Usage: event <desc> /from <time> /to <time>.",
        "Event description missing before '/from'. Usage: event <desc> /from <time> /to <time>.",
        "Event start time missing after '/from'. Usage: event <desc> /from <time> /to <time>.",
        "Event end time missing after '/to'. Usage: event <desc> /from <time> /to <time>.",
        "Event times are in the wrong order. Put '/from' before '/to'. Usage: event <desc> /from <time> /to <time>.",
        "An event must contain exactly one '/from' and one '/to' separator. Usage: event <desc> /from <time> /to <time>.",
        "Task added!`n[D][ ] revise notes (by: Friday)`nNow you have 1 tasks in the list",
        "Task added!`n[E][ ] lab (from: 10am to: 12pm)`nNow you have 2 tasks in the list",
        "Here are the tasks in your list:`n1.[D][ ] revise notes (by: Friday)`n2.[E][ ] lab (from: 10am to: 12pm)",
        'Bye! Have a nice day!'
    )
    $malformedAdditionOutput = "$welcome`n$divider`n" +
            ($malformedAdditionMessages -join "`n$divider`n$divider`n") + "`n$divider"

    $invalidIndexCommands = @(
        'mark 1', 'todo read book', 'mark', 'unmark one', 'mark 2147483648',
        'mark 0', 'mark 2', 'mark 1 extra', 'mark 1', 'list', 'unmark 1', 'list', 'bye'
    )
    $invalidIndexMessages = @(
        "Cannot mark a task because the task list is empty. Add a task first, then use 'mark <task number>'.",
        "Task added!`n[T][ ] read book`nNow you have 1 tasks in the list",
        "Task number missing. Usage: mark <task number>, for example 'mark 1'.",
        "Invalid task number 'one'. Enter one whole number, for example 'unmark 1'.",
        "Invalid task number '2147483648'. Enter one whole number, for example 'mark 1'.",
        "Task number must be at least 1. Use 'list' to see valid task numbers.",
        "Task 2 does not exist. Choose a number from 1 to 1. Use 'list' to see the tasks.",
        "Invalid task number '1 extra'. Enter one whole number, for example 'mark 1'.",
        "Task marked as complete!`n[T][X] read book",
        "Here are the tasks in your list:`n1.[T][X] read book",
        "Task marked as incomplete`n[T][ ] read book",
        "Here are the tasks in your list:`n1.[T][ ] read book",
        'Bye! Have a nice day!'
    )
    $invalidIndexOutput = "$welcome`n$divider`n" +
            ($invalidIndexMessages -join "`n$divider`n$divider`n") + "`n$divider"

    $capacityCommands = @(1..100 | ForEach-Object { "todo task $_" }) + @('todo overflow task', 'list', 'bye')
    $capacityMessages = @(1..100 | ForEach-Object {
        "Task added!`n[T][ ] task $_`nNow you have $_ tasks in the list"
    })
    $capacityMessages += 'Task list is full (maximum 100 tasks). No task was added.'
    $capacityTaskList = @(1..100 | ForEach-Object { "$_.[T][ ] task $_" })
    $capacityMessages += "Here are the tasks in your list:`n" + ($capacityTaskList -join "`n")
    $capacityMessages += 'Bye! Have a nice day!'
    $capacityOutput = "$welcome`n$divider`n" + ($capacityMessages -join "`n$divider`n$divider`n") + "`n$divider"

    $cases = @(
        @{ Name = 'startup and exit'; Commands = @('bye'); ExpectedOutputs = @("____________________________________________________________`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nHello! I'm MoistBot`nHow can I help you today?`n____________________________________________________________`n____________________________________________________________`nBye! Have a nice day!`n____________________________________________________________") },
        @{ Name = 'empty list'; Commands = @('list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nHere are the tasks in your list:`n$divider`n$exitMessage") },
        @{ Name = 'add todo and list'; Commands = @('todo buy milk', 'list', 'bye'); ExpectedOutputs = @("____________________________________________________________`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nHello! I'm MoistBot`nHow can I help you today?`n____________________________________________________________`n____________________________________________________________`nTask added!`n[T][ ] buy milk`nNow you have 1 tasks in the list`n____________________________________________________________`n____________________________________________________________`nHere are the tasks in your list:`n1.[T][ ] buy milk`n____________________________________________________________`n____________________________________________________________`nBye! Have a nice day!`n____________________________________________________________") },
        @{ Name = 'add deadline, event, mark, and unmark'; Commands = @('todo read book', 'deadline return book /by Friday', 'event team meeting /from 2pm /to 4pm', 'mark 2', 'list', 'unmark 2', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nTask added!`n[T][ ] read book`nNow you have 1 tasks in the list`n$divider`n$divider`nTask added!`n[D][ ] return book (by: Friday)`nNow you have 2 tasks in the list`n$divider`n$divider`nTask added!`n[E][ ] team meeting (from: 2pm to: 4pm)`nNow you have 3 tasks in the list`n$divider`n$divider`nTask marked as complete!`n[D][X] return book (by: Friday)`n$divider`n$divider`nHere are the tasks in your list:`n1.[T][ ] read book`n2.[D][X] return book (by: Friday)`n3.[E][ ] team meeting (from: 2pm to: 4pm)`n$divider`n$divider`nTask marked as incomplete`n[D][ ] return book (by: Friday)`n$divider`n$exitMessage") },
        @{ Name = 'unrecognised command'; Commands = @('buy groceries today', 'list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nUnknown command 'buy'. Available commands: bye, list, todo, deadline, event, mark, unmark.`n$divider`n$divider`nHere are the tasks in your list:`n$divider`n$exitMessage") },
        @{ Name = 'malformed additions explain the required correction'; Commands = $malformedAdditionCommands; ExpectedOutputs = @($malformedAdditionOutput) },
        @{ Name = 'invalid mark and unmark preserve completion state'; Commands = $invalidIndexCommands; ExpectedOutputs = @($invalidIndexOutput) },
        @{ Name = 'blank input is handled as a chatbot error'; Commands = @('', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nInput cannot be empty. Enter a command such as 'list' or 'todo buy milk'.`n$divider`n$exitMessage") },
        @{ Name = 'argument-free commands reject extra text'; Commands = @('list now', 'bye now', 'list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nThe 'list' command does not accept arguments. Enter only 'list'.`n$divider`n$divider`nThe 'bye' command does not accept arguments. Enter only 'bye'.`n$divider`n$divider`nHere are the tasks in your list:`n$divider`n$exitMessage") },
        @{ Name = 'task capacity error preserves the list'; Commands = $capacityCommands; ExpectedOutputs = @($capacityOutput) }
    )
}

Write-Host "Test plan: $PlanFile"
foreach ($case in $cases) {
    if ($case.ContainsKey('ExpectedOutputs')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -caseExpectedOutput $case.ExpectedOutputs
    } else {
        Run-Case -name $case.Name -caseCommands $case.Commands -caseExpectedOutput @($case.ExpectedOutput)
    }
}

Write-Host 'All UI checks passed.' -ForegroundColor Green
