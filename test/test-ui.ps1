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

    $cases = @(
        @{ Name = 'startup and exit'; Commands = @('bye'); ExpectedOutputs = @("____________________________________________________________`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nHello! I'm MoistBot`nHow can I help you today?`n____________________________________________________________`n____________________________________________________________`nBye! Have a nice day!`n____________________________________________________________") },
        @{ Name = 'add todo and list'; Commands = @('todo buy milk', 'list', 'bye'); ExpectedOutputs = @("____________________________________________________________`n __  __   ___   ___ ____ _____ ____   ___ _____`n|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|`n| |\/| || | | | | | \___ \ | | |  _ \| | | | | |`n| |  | || |_| | | |  ___) || | | |_) | |_| | | |`n|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_|`nHello! I'm MoistBot`nHow can I help you today?`n____________________________________________________________`n____________________________________________________________`nTask added!`n[T][ ] buy milk`nNow you have 1 tasks in the list`n____________________________________________________________`n____________________________________________________________`nHere are the tasks in your list:`n1.[T][ ] buy milk`n____________________________________________________________`n____________________________________________________________`nBye! Have a nice day!`n____________________________________________________________") },
        @{ Name = 'unrecognised command'; Commands = @('buy groceries today', 'list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nUnknown command: buy`n$divider`n$divider`nHere are the tasks in your list:`n$divider`n$exitMessage") },
        @{ Name = 'malformed additions preserve tasks'; Commands = @('todo submit assignment', 'todo', 'deadline pay bills /by', 'deadline revise notes /by Friday', 'event meeting /from 2pm', 'event lab /from 10am /to 12pm', 'list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nTask added!`n[T][ ] submit assignment`nNow you have 1 tasks in the list`n$divider`n$divider`nDescription missing. Usage: todo <description>`n$divider`n$divider`nInvalid command. Usage: deadline <desc> /by <time>`n$divider`n$divider`nTask added!`n[D][ ] revise notes (by: Friday)`nNow you have 2 tasks in the list`n$divider`n$divider`nInvalid command. Usage: event <desc> /from <time> /to <time>`n$divider`n$divider`nTask added!`n[E][ ] lab (from: 10am to: 12pm)`nNow you have 3 tasks in the list`n$divider`n$divider`nHere are the tasks in your list:`n1.[T][ ] submit assignment`n2.[D][ ] revise notes (by: Friday)`n3.[E][ ] lab (from: 10am to: 12pm)`n$divider`n$exitMessage") },
        @{ Name = 'invalid mark and unmark preserve completion state'; Commands = @('todo read book', 'mark 1', 'mark 2', 'unmark one', 'list', 'unmark 1', 'unmark 0', 'mark 1 extra', 'list', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nTask added!`n[T][ ] read book`nNow you have 1 tasks in the list`n$divider`n$divider`nTask marked as complete!`n[T][X] read book`n$divider`n$divider`nTask not found`n$divider`n$divider`nPlease provide an integer argument`n$divider`n$divider`nHere are the tasks in your list:`n1.[T][X] read book`n$divider`n$divider`nTask marked as incomplete`n[T][ ] read book`n$divider`n$divider`nTask not found`n$divider`n$divider`nPlease provide an integer argument`n$divider`n$divider`nHere are the tasks in your list:`n1.[T][ ] read book`n$divider`n$exitMessage") },
        @{ Name = 'blank input is handled as a chatbot error'; Commands = @('', 'bye'); ExpectedOutputs = @("$welcome`n$divider`nPlease provide an input`n$divider`n$exitMessage") }
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
