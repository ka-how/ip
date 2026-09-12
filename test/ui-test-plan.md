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

## Test case 11: task changes are saved to disk

Aim: Confirm that additions and completion-status changes rewrite the saved
task list using a stable representation. This is verified as the closest
automated check because saving does not add console output.

Inputs: `todo read book`, `deadline return book /by Friday`,
`event meeting /from 2pm /to 4pm`, `todo compare A | B`, `mark 2`, `bye`

Expected file at `data/moistbot.txt`:

```text
T | 0 | read book
D | 1 | return book | Friday
E | 0 | meeting | 2pm | 4pm
T | 0 | compare A \| B
```

Expected console output: The existing successful add and mark confirmations,
followed by the shared farewell. Saving produces no additional console output.

## Test case 12: saved tasks are loaded at startup

Aim: Confirm that MoistBot restores todo, deadline, and event tasks together
with their completion states before accepting the first command.

Initial file at `data/moistbot.txt` (with an optional UTF-8 byte-order mark):

```text
T | 1 | read book
D | 0 | return book | Friday
E | 0 | meeting | 2pm | 4pm
T | 0 | review A \| B \\ notes
```

Inputs: `list`, `bye`

Expected output:

```text
Certainly. Here is your task list:
1.[T][X] read book
2.[D][ ] return book (by: Friday)
3.[E][ ] meeting (from: 2pm to: 4pm)
4.[T][ ] review A | B \ notes
```

The shared farewell follows. Loading produces no additional console output.

## Test case 13: corrupted saved data is reported safely

Aim: Confirm that corrupted saved data does not crash MoistBot or partially
populate the task list, and that the user receives an actionable explanation
identifying the first invalid line.

Initial-file variants:

- Invalid completion flag: `T | maybe | read book`
- Unknown task type: `X | 0 | read book`
- Missing field: `D | 0 | return book`
- Extra field: `T | 0 | read book | extra`
- Blank required field: `E | 0 | meeting | 2pm | `
- Blank line between otherwise valid records
- A valid first record followed by malformed line 2
- Invalid UTF-8 bytes

Inputs: `list`, `bye`

Expected output:

```text
My apologies, but line <number> in the save file is invalid. I have started with an empty task list instead. Please add your tasks again; MoistBot will replace the save file when the task list next changes.
Certainly. Here is your task list:
Your task list is presently empty. You may use: bye, list, todo, deadline, event, mark, or unmark.
```

For invalid UTF-8, the unreadable-save-file message from test case 15 is
shown instead. In every variant, the list remains empty and the shared
farewell follows.

## Test case 14: missing save paths start safely

Aim: Confirm that MoistBot starts with an empty task list when either the
relative `data` folder or `data/moistbot.txt` does not yet exist.

Setup: Run once without the `data` folder, and once with an empty `data`
folder but no save file.

Inputs: `list`, `bye`

Expected output: The empty-list response from test case 2, followed by the
shared farewell. No storage error is shown.

## Test case 15: invalid save-file path is reported safely

Aim: Confirm that an unreadable save-file path does not crash MoistBot and
that a task addition is rolled back when it cannot be saved.

Setup: Create a directory at the relative `data/moistbot.txt` path.

Inputs: `todo buy milk`, `list`, `bye`

Expected output:

```text
My apologies, but I could not read your saved task list. Please check that the save file is readable, then restart MoistBot.
My apologies, but I could not save your task list. Please check that the data folder is writable, then try your command again.
```

No addition confirmation is shown. The following `list` response remains
empty, confirming that the unsaved task was rolled back, and MoistBot
continues to the shared farewell.

## Test case 16: oversized saved list is rejected atomically

Aim: Confirm that a save file exceeding the current 100-task capacity is
rejected before any tasks are inserted.

Setup: Populate `data/moistbot.txt` with 101 valid todo records.

Inputs: `list`, `bye`

Expected output:

```text
My apologies, but the save file contains more tasks than MoistBot can hold. Please reduce the number of saved tasks, then restart MoistBot.
```

The list remains empty and MoistBot continues to the shared farewell.
