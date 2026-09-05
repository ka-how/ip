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
