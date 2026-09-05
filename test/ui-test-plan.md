# UI test plan for MoistBot

This plan records the console UI checks for the MoistBot command-line app. Each case lists the test aim, the inputs to send, and the expected output to verify.

## General test procedure

- Compile the Java sources into `out/`.
- Launch `MoistBot` with the input commands listed for the test case.
- Capture the full console transcript, including both input and output.
- Compare the actual console output against the expected output.
- If a mismatch is found, stop the session immediately and report the actual vs expected output.

## Test case 1: startup and exit

Aim: Confirm that the welcome banner is printed and the app exits cleanly when the user enters `bye`.

Inputs:
- `bye`

Expected output:
```text
____________________________________________________________
 __  __   ___   ___ ____ _____ ____   ___ _____
|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|
| |\/| || | | | | | \___ \ | | |  _ \| | | | | |
| |  | || |_| | | |  ___) || | | |_) | |_| | | |
|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_| 
Hello! I'm MoistBot
How can I help you today?
____________________________________________________________
____________________________________________________________
Bye! Have a nice day!
____________________________________________________________
```

## Test case 2: empty list

Aim: Confirm that the app prints the empty-task list message without errors.

Inputs:
- `list`
- `bye`

Expected output:
```text
____________________________________________________________
 __  __   ___   ___ ____ _____ ____   ___ _____
|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|
| |\/| || | | | | | \___ \ | | |  _ \| | | | | |
| |  | || |_| | | |  ___) || | | |_) | |_| | | |
|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_| 
Hello! I'm MoistBot
How can I help you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye! Have a nice day!
____________________________________________________________
```

## Test case 3: add todo and list

Aim: Verify that a todo is added correctly and then displayed in the task list.

Inputs:
- `todo buy milk`
- `list`
- `bye`

Expected output:
```text
____________________________________________________________
 __  __   ___   ___ ____ _____ ____   ___ _____
|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|
| |\/| || | | | | | \___ \ | | |  _ \| | | | | |
| |  | || |_| | | |  ___) || | | |_) | |_| | | |
|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_| 
Hello! I'm MoistBot
How can I help you today?
____________________________________________________________
____________________________________________________________
Task added!
[T][ ] buy milk
Now you have 1 tasks in the list
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye! Have a nice day!
____________________________________________________________
```

## Test case 4: add deadline, event, mark, and unmark

Aim: Check that deadline and event tasks are parsed and listed, and that the completion toggle works correctly.

Inputs:
- `todo read book`
- `deadline return book /by Friday`
- `event team meeting /from 2pm /to 4pm`
- `mark 2`
- `list`
- `unmark 2`
- `bye`

Expected output:
```text
____________________________________________________________
 __  __   ___   ___ ____ _____ ____   ___ _____
|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|
| |\/| || | | | | | \___ \ | | |  _ \| | | | | |
| |  | || |_| | | |  ___) || | | |_) | |_| | | |
|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_| 
Hello! I'm MoistBot
How can I help you today?
____________________________________________________________
____________________________________________________________
Task added!
[T][ ] read book
Now you have 1 tasks in the list
____________________________________________________________
____________________________________________________________
Task added!
[D][ ] return book (by: Friday)
Now you have 2 tasks in the list
____________________________________________________________
____________________________________________________________
Task added!
[E][ ] team meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list
____________________________________________________________
____________________________________________________________
Task marked as complete!
[D][X] return book (by: Friday)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Friday)
3.[E][ ] team meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Task marked as incomplete
[D][ ] return book (by: Friday)
____________________________________________________________
____________________________________________________________
Bye! Have a nice day!
____________________________________________________________
```

## Test case 5: unrecognised command

Aim: Verify that an unrecognised command prints an error and does not add a task.

Inputs:
- `buy groceries today`
- `list`
- `bye`

Expected output:
```text
____________________________________________________________
Unknown command: buy
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
```

## Test case 6: malformed additions preserve existing tasks

Aim: Verify that malformed `todo`, `deadline`, and `event` commands are
rejected without adding partial tasks, even when they are interleaved with
valid additions.

Inputs:
- `todo submit assignment`
- `todo`
- `deadline pay bills /by`
- `deadline revise notes /by Friday`
- `event meeting /from 2pm`
- `event lab /from 10am /to 12pm`
- `list`
- `bye`

Expected output after the welcome banner:
```text
Task added!
[T][ ] submit assignment
Now you have 1 tasks in the list
Description missing. Usage: todo <description>
Invalid command. Usage: deadline <desc> /by <time>
Task added!
[D][ ] revise notes (by: Friday)
Now you have 2 tasks in the list
Invalid command. Usage: event <desc> /from <time> /to <time>
Task added!
[E][ ] lab (from: 10am to: 12pm)
Now you have 3 tasks in the list
Here are the tasks in your list:
1.[T][ ] submit assignment
2.[D][ ] revise notes (by: Friday)
3.[E][ ] lab (from: 10am to: 12pm)
Bye! Have a nice day!
```

## Test case 7: invalid mark and unmark preserve completion state

Aim: Verify that invalid task indexes and non-integer arguments do not change
the completion state set by valid `mark` and `unmark` commands.

Inputs:
- `todo read book`
- `mark 1`
- `mark 2`
- `unmark one`
- `list`
- `unmark 1`
- `unmark 0`
- `mark 1 extra`
- `list`
- `bye`

Expected output after the welcome banner:
```text
Task added!
[T][ ] read book
Now you have 1 tasks in the list
Task marked as complete!
[T][X] read book
Task not found
Please provide an integer argument
Here are the tasks in your list:
1.[T][X] read book
Task marked as incomplete
[T][ ] read book
Task not found
Please provide an integer argument
Here are the tasks in your list:
1.[T][ ] read book
Bye! Have a nice day!
```

## Test case 8: blank input is handled as a chatbot error

Aim: Verify that a blank command is reported through the chatbot's normal
error-handling path and that the app continues accepting commands.

Inputs:
- *(blank line)*
- `bye`

Expected output after the welcome banner:
```text
Please provide an input
Bye! Have a nice day!
```

## Example transcript format

A valid test transcript should show the exact console input and output used during a session, for example:

```text
Input:
bye

Output:
____________________________________________________________
 __  __   ___   ___ ____ _____ ____   ___ _____
|  \/  | / _ \ |_ _|/ ___|_   _| __ ) / _ \|_   _|
| |\/| || | | | | | \___ \ | | |  _ \| | | | | |
| |  | || |_| | | |  ___) || | | |_) | |_| | | |
|_|  |_| \___/ |___||____/ |_| |____/ \___/  |_| 
Hello! I'm MoistBot
How can I help you today?
____________________________________________________________
____________________________________________________________
Bye! Have a nice day!
____________________________________________________________
```
