param(
    [string[]]$Commands = @(),
    [string[]]$ExpectedOutputs = @(),
    [string]$PlanFile = (Join-Path $PSScriptRoot 'ui-test-plan.md')
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$srcDir = Join-Path $projectRoot 'src\main\java'
$outDir = Join-Path $projectRoot 'out'
$testWorkDir = Join-Path $projectRoot '_temp\ui-test'
$dataDir = Join-Path $testWorkDir 'data'
$dataFile = Join-Path $dataDir 'moistbot.txt'
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

function Run-Case(
        [string]$name,
        [string[]]$caseCommands,
        [string]$expected,
        [string]$expectedStorage = $null,
        [string]$initialStorage = $null,
        [byte[]]$initialStorageBytes = $null,
        [switch]$initialDataDirectory,
        [switch]$initialStorageIsDirectory) {
    $shouldCheckStorage = $PSBoundParameters.ContainsKey('expectedStorage')
    $shouldPrepareStorage = $PSBoundParameters.ContainsKey('initialStorage')
    $shouldPrepareStorageBytes = $PSBoundParameters.ContainsKey('initialStorageBytes')
    Write-Host "=== Running $name ==="
    Write-Host 'Input:'
    $caseCommands | ForEach-Object { Write-Host $_ }

    if (-not (Test-Path $outDir)) {
        New-Item -ItemType Directory -Path $outDir -Force | Out-Null
    }

    if (-not (Test-Path $testWorkDir)) {
        New-Item -ItemType Directory -Path $testWorkDir -Force | Out-Null
    }
    if (Test-Path $dataFile) {
        Remove-Item -LiteralPath $dataFile
    }
    if ((Test-Path $dataDir) -and @(Get-ChildItem -LiteralPath $dataDir).Count -eq 0) {
        Remove-Item -LiteralPath $dataDir
    }
    if ($initialDataDirectory) {
        New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
    }
    if ($shouldPrepareStorage) {
        New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
        [System.IO.File]::WriteAllText($dataFile, $initialStorage, [System.Text.UTF8Encoding]::new($false))
    }
    if ($shouldPrepareStorageBytes) {
        New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
        [System.IO.File]::WriteAllBytes($dataFile, $initialStorageBytes)
    }
    if ($initialStorageIsDirectory) {
        New-Item -ItemType Directory -Path $dataFile -Force | Out-Null
    }

    $javaFiles = Get-ChildItem -Path $srcDir -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName
    javac -d $outDir $javaFiles *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed while building MoistBot before running $name."
    }

    Push-Location $testWorkDir
    try {
        $actual = @($caseCommands) | java -cp $outDir moistbot.MoistBot 2>&1 | Out-String
    } finally {
        Pop-Location
    }
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

    if ($shouldCheckStorage) {
        if (-not (Test-Path $dataFile)) {
            Write-Host "FAILED: $name" -ForegroundColor Red
            Write-Host "Expected storage file was not created: $dataFile" -ForegroundColor Yellow
            exit 1
        }

        $actualStorage = Normalize-Output (Get-Content -Raw $dataFile)
        $expectedStorageText = Normalize-Output $expectedStorage
        if ($actualStorage -ne $expectedStorageText) {
            Write-Host "FAILED: $name" -ForegroundColor Red
            Write-Host 'Expected storage contents:' -ForegroundColor Yellow
            Write-Host $expectedStorageText
            Write-Host ''
            Write-Host 'Actual storage contents:' -ForegroundColor Yellow
            Write-Host $actualStorage
            exit 1
        }
        Write-Host 'Saved data:'
        Write-Host $actualStorage
        Write-Host ''
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

$storageCommands = @(
    'todo read book', 'deadline return book /by Friday', 'event meeting /from 2pm /to 4pm',
    'todo compare A | B', 'mark 2', 'bye')
$storageMessages = @(
    "Certainly. I have added this task:`n[T][ ] read book`nYour list now contains 1 task.",
    "Certainly. I have added this task:`n[D][ ] return book (by: Friday)`nYour list now contains 2 tasks.",
    "Certainly. I have added this task:`n[E][ ] meeting (from: 2pm to: 4pm)`nYour list now contains 3 tasks.",
    "Certainly. I have added this task:`n[T][ ] compare A | B`nYour list now contains 4 tasks.",
    "Certainly. I have marked this task as complete:`n[D][X] return book (by: Friday)", $exitMessage)
$storageContents = "T | 0 | read book`nD | 1 | return book | Friday" `
        + "`nE | 0 | meeting | 2pm | 4pm`nT | 0 | compare A \| B"
$cases += @{
    Name = 'task changes are saved to disk'
    Commands = $storageCommands
    Messages = $storageMessages
    Storage = $storageContents
}

$loadedStorage = [char]0xFEFF + "T | 1 | read book`nD | 0 | return book | Friday" `
        + "`nE | 0 | meeting | 2pm | 4pm`nT | 0 | review A \| B \\ notes"
$loadedListMessage = "Certainly. Here is your task list:`n1.[T][X] read book" `
        + "`n2.[D][ ] return book (by: Friday)`n3.[E][ ] meeting (from: 2pm to: 4pm)" `
        + "`n4.[T][ ] review A | B \ notes"
$cases += @{
    Name = 'saved tasks are loaded at startup'
    Commands = @('list', 'bye')
    Messages = @($loadedListMessage, $exitMessage)
    InitialStorage = $loadedStorage
}

$corruptedStorageCases = @(
    @{ Name = 'invalid completion flag'; Storage = 'T | maybe | read book'; Line = 1 },
    @{ Name = 'unknown task type'; Storage = 'X | 0 | read book'; Line = 1 },
    @{ Name = 'missing task field'; Storage = 'D | 0 | return book'; Line = 1 },
    @{ Name = 'extra task field'; Storage = 'T | 0 | read book | extra'; Line = 1 },
    @{ Name = 'blank required field'; Storage = 'E | 0 | meeting | 2pm | '; Line = 1 },
    @{ Name = 'blank record'; Storage = "T | 0 | read book`n `nD | 0 | return book | Friday"; Line = 2 },
    @{ Name = 'valid record before corruption'; Storage = "T | 0 | read book`nD | broken"; Line = 2 }
)
foreach ($corruptedStorageCase in $corruptedStorageCases) {
    $invalidStorageMessage = 'My apologies, but line ' + $corruptedStorageCase.Line `
            + ' in the save file is invalid. I have started with an empty task list instead. Please add your ' `
            + 'tasks again; MoistBot will replace the save file when the task list next changes.'
    $cases += @{
        Name = "corrupted save file: $($corruptedStorageCase.Name)"
        Commands = @('list', 'bye')
        Messages = @(
            $invalidStorageMessage,
            "Certainly. Here is your task list:`n$emptyListMessage",
            $exitMessage)
        InitialStorage = $corruptedStorageCase.Storage
    }
}

$readErrorMessage = 'My apologies, but I could not read your saved task list. Please check that the save file ' `
        + 'is readable, then restart MoistBot.'
$cases += @{
    Name = 'corrupted save file: invalid UTF-8'
    Commands = @('list', 'bye')
    Messages = @(
        $readErrorMessage,
        "Certainly. Here is your task list:`n$emptyListMessage",
        $exitMessage)
    InitialStorageBytes = [byte[]](0x54, 0x20, 0x7c, 0x20, 0x30, 0x20, 0x7c, 0x20, 0xc3, 0x28)
}

$missingFileMessages = @(
    "Certainly. Here is your task list:`n$emptyListMessage",
    $exitMessage)
$cases += @{
    Name = 'missing save file in an existing folder starts empty'
    Commands = @('list', 'bye')
    Messages = $missingFileMessages
    InitialDataDirectory = $true
}

$saveErrorMessage = 'My apologies, but I could not save your task list. Please check that the data folder is ' `
        + 'writable, then try your command again.'
$cases += @{
    Name = 'invalid save file path is reported safely'
    Commands = @('todo buy milk', 'list', 'bye')
    Messages = @(
        $readErrorMessage,
        $saveErrorMessage,
        "Certainly. Here is your task list:`n$emptyListMessage",
        $exitMessage)
    InitialStorageIsDirectory = $true
}

$excessTaskStorage = @(1..101 | ForEach-Object { "T | 0 | task $_" }) -join "`n"
$excessTaskMessage = 'My apologies, but the save file contains more tasks than MoistBot can hold. Please reduce ' `
        + 'the number of saved tasks, then restart MoistBot.'
$cases += @{
    Name = 'oversized saved list is rejected atomically'
    Commands = @('list', 'bye')
    Messages = @(
        $excessTaskMessage,
        "Certainly. Here is your task list:`n$emptyListMessage",
        $exitMessage)
    InitialStorage = $excessTaskStorage
}

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
    if ($case.ContainsKey('InitialStorage')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages) `
                -initialStorage $case.InitialStorage
    } elseif ($case.ContainsKey('InitialStorageBytes')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages) `
                -initialStorageBytes $case.InitialStorageBytes
    } elseif ($case.ContainsKey('InitialDataDirectory')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages) `
                -initialDataDirectory
    } elseif ($case.ContainsKey('InitialStorageIsDirectory')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages) `
                -initialStorageIsDirectory
    } elseif ($case.ContainsKey('Storage')) {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages) `
                -expectedStorage $case.Storage
    } else {
        Run-Case -name $case.Name -caseCommands $case.Commands -expected (Format-Session $case.Messages)
    }
}

Write-Host 'All UI checks passed.' -ForegroundColor Green
