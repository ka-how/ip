# UI test plan for MoistBot

Each case is run by `test/test-ui.ps1`, which verifies the full, exact console
transcript (including banner and dividers) and stops at the first mismatch.

## Shared output framing

Every transcript begins with the banner followed by:

```text
Good day. I am MoistBot, at your service.
How may I assist you today?
```

Each response is surrounded by the underscore divider. A successful `bye`
command ends with:

```text
Thank you for using MoistBot. Have a pleasant day.
```

## Test case 1: startup and exit

Aim: Confirm that the professional greeting and farewell are displayed.

Inputs: `bye`

Expected output: The shared greeting, followed by the shared farewell.

## Test case 2: empty list

Aim: Confirm that an empty list is described courteously.

Inputs: `list`, `bye`

Expected output:

```text
Certainly. Here is your task list:
Your task list is presently empty. You may use: bye, list, todo, deadline, event, mark, or unmark.
```

## Test case 3: add todo and list

Aim: Confirm that adding and viewing a todo uses the courteous confirmation.

Inputs: `todo buy milk`, `list`, `bye`

Expected output:

```text
Certainly. I have added this task:
[T][ ] buy milk
Your list now contains 1 task.
Certainly. Here is your task list:
1.[T][ ] buy milk
```

## Test case 4: add deadline, event, mark, and unmark

Aim: Confirm that all successful task operations retain their behavior and use
the new tone.

Inputs: `todo read book`, `deadline return book /by Friday`,
`event team meeting /from 2pm /to 4pm`, `mark 2`, `list`, `unmark 2`, `bye`

Expected output: Each addition begins “Certainly. I have added this task:”,
the deadline is marked complete then incomplete, and the list contains all
three tasks.

## Test case 5: unrecognised command

Aim: Confirm that an unknown command is declined politely without adding work.

Inputs: `buy groceries today`, `list`, `bye`

Expected output:

```text
My apologies, but I do not recognise the command 'buy'. Available commands are: bye, list, todo, deadline, event, mark, and unmark.
```

The following list remains empty as in test case 2.

## Test case 6: malformed additions

Aim: Confirm that malformed todo, deadline, and event commands provide polite,
actionable corrections and do not add tasks.

Inputs: the malformed command sequence in `test/test-ui.ps1` test case 6,
followed by valid deadline and event commands, `list`, and `bye`.

Expected output: Missing fields begin with “Please provide” or “Please
include”; duplicate separators explain that only one is allowed. The final list
contains only `revise notes` and `lab`.

## Test case 7: invalid mark and unmark

Aim: Confirm that invalid task numbers are declined politely and do not change
the completion state.

Inputs: the invalid index sequence in `test/test-ui.ps1` test case 7.

Expected output: The empty-list, non-numeric, out-of-range, and malformed
arguments explain the correction. Only `mark 1` and `unmark 1` change the
status of `read book`.

## Test case 8: blank input

Aim: Confirm that a blank command is handled politely and the program remains
available.

Inputs: blank line, `bye`

Expected output:

```text
Please enter a command, such as 'list' or 'todo buy milk'.
```

## Test case 9: arguments for argument-free commands

Aim: Confirm that `list` and `bye` reject extra text politely.

Inputs: `list now`, `bye now`, `list`, `bye`

Expected output:

```text
The 'list' command does not accept arguments. Please enter only 'list'.
The 'bye' command does not accept arguments. Please enter only 'bye'.
```

The app then prints the empty list and continues to the farewell.

## Test case 10: task capacity

Aim: Confirm that the 101st task is declined politely and the first 100 tasks
remain intact.

Inputs: `todo task 1` through `todo task 100`, `todo overflow task`, `list`,
`bye`.

Expected output: Each successful addition says “Certainly. I have added this
task:” and reports the grammatically correct singular or plural count. The
overflow response is:

```text
My apologies, but your task list is full (maximum 100 tasks). No task has been added.
```

The final list contains tasks 1 through 100 only.
