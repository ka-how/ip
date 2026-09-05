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
Unknown command 'buy'. Available commands: bye, list, todo, deadline, event, mark, unmark.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
```

## Test case 6: malformed additions explain the required correction

Aim: Verify that each missing or duplicated task field is identified precisely,
the correct syntax is shown, and malformed commands do not add tasks.

Inputs:
- `todo`
- `deadline`
- `deadline pay bills`
- `deadline /by Friday`
- `deadline pay bills /by`
- `deadline pay bills /by Friday /by Saturday`
- `event meeting /from 2pm`
- `event meeting /to 4pm`
- `event /from 2pm /to 4pm`
- `event meeting /from /to 4pm`
- `event meeting /from 2pm /to`
- `event meeting /to 4pm /from 2pm`
- `event meeting /from 1pm /from 2pm /to 4pm`
- `deadline revise notes /by Friday`
- `event lab /from 10am /to 12pm`
- `list`
- `bye`

Expected output after the welcome banner:
```text
Todo description missing. Usage: todo <description>, for example 'todo buy milk'.
Deadline description and time missing. Usage: deadline <desc> /by <time>, for example 'deadline return book /by Friday'.
Deadline separator '/by' missing. Usage: deadline <desc> /by <time>.
Deadline description missing before '/by'. Usage: deadline <desc> /by <time>.
Deadline time missing after '/by'. Usage: deadline <desc> /by <time>.
A deadline can contain only one '/by' separator. Usage: deadline <desc> /by <time>.
Event end separator '/to' missing. Usage: event <desc> /from <time> /to <time>.
Event start separator '/from' missing. Usage: event <desc> /from <time> /to <time>.
Event description missing before '/from'. Usage: event <desc> /from <time> /to <time>.
Event start time missing after '/from'. Usage: event <desc> /from <time> /to <time>.
Event end time missing after '/to'. Usage: event <desc> /from <time> /to <time>.
Event times are in the wrong order. Put '/from' before '/to'. Usage: event <desc> /from <time> /to <time>.
An event must contain exactly one '/from' and one '/to' separator. Usage: event <desc> /from <time> /to <time>.
Task added!
[D][ ] revise notes (by: Friday)
Now you have 1 tasks in the list
Task added!
[E][ ] lab (from: 10am to: 12pm)
Now you have 2 tasks in the list
Here are the tasks in your list:
1.[D][ ] revise notes (by: Friday)
2.[E][ ] lab (from: 10am to: 12pm)
Bye! Have a nice day!
```

## Test case 7: invalid mark and unmark preserve completion state

Aim: Verify that invalid task indexes and non-integer arguments do not change
the completion state set by valid `mark` and `unmark` commands.

Inputs:
- `mark 1`
- `todo read book`
- `mark`
- `unmark one`
- `mark 2147483648`
- `mark 0`
- `mark 2`
- `mark 1 extra`
- `mark 1`
- `list`
- `unmark 1`
- `list`
- `bye`

Expected output after the welcome banner:
```text
Cannot mark a task because the task list is empty. Add a task first, then use 'mark <task number>'.
Task added!
[T][ ] read book
Now you have 1 tasks in the list
Task number missing. Usage: mark <task number>, for example 'mark 1'.
Invalid task number 'one'. Enter one whole number, for example 'unmark 1'.
Invalid task number '2147483648'. Enter one whole number, for example 'mark 1'.
Task number must be at least 1. Use 'list' to see valid task numbers.
Task 2 does not exist. Choose a number from 1 to 1. Use 'list' to see the tasks.
Invalid task number '1 extra'. Enter one whole number, for example 'mark 1'.
Task marked as complete!
[T][X] read book
Here are the tasks in your list:
1.[T][X] read book
Task marked as incomplete
[T][ ] read book
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
Input cannot be empty. Enter a command such as 'list' or 'todo buy milk'.
Bye! Have a nice day!
```

## Test case 9: argument-free commands reject extra text

Aim: Verify that `list` and `bye` reject unexpected arguments, explain the
correction, and keep the application running after an invalid `bye` command.

Inputs:
- `list now`
- `bye now`
- `list`
- `bye`

Expected output after the welcome banner:
```text
The 'list' command does not accept arguments. Enter only 'list'.
The 'bye' command does not accept arguments. Enter only 'bye'.
Here are the tasks in your list:
Bye! Have a nice day!
```

## Test case 10: task capacity error preserves the list

Aim: Verify that adding a 101st task reports the exact 100-task limit, explains
that the task was not added, and leaves all existing tasks intact.

Inputs:
- `todo task 1` through `todo task 100`, in ascending order
- `todo overflow task`
- `list`
- `bye`

Expected output after the welcome banner:
```text
Task added!
[T][ ] task 1
Now you have 1 tasks in the list
Task added!
[T][ ] task 2
Now you have 2 tasks in the list
Task added!
[T][ ] task 3
Now you have 3 tasks in the list
Task added!
[T][ ] task 4
Now you have 4 tasks in the list
Task added!
[T][ ] task 5
Now you have 5 tasks in the list
Task added!
[T][ ] task 6
Now you have 6 tasks in the list
Task added!
[T][ ] task 7
Now you have 7 tasks in the list
Task added!
[T][ ] task 8
Now you have 8 tasks in the list
Task added!
[T][ ] task 9
Now you have 9 tasks in the list
Task added!
[T][ ] task 10
Now you have 10 tasks in the list
Task added!
[T][ ] task 11
Now you have 11 tasks in the list
Task added!
[T][ ] task 12
Now you have 12 tasks in the list
Task added!
[T][ ] task 13
Now you have 13 tasks in the list
Task added!
[T][ ] task 14
Now you have 14 tasks in the list
Task added!
[T][ ] task 15
Now you have 15 tasks in the list
Task added!
[T][ ] task 16
Now you have 16 tasks in the list
Task added!
[T][ ] task 17
Now you have 17 tasks in the list
Task added!
[T][ ] task 18
Now you have 18 tasks in the list
Task added!
[T][ ] task 19
Now you have 19 tasks in the list
Task added!
[T][ ] task 20
Now you have 20 tasks in the list
Task added!
[T][ ] task 21
Now you have 21 tasks in the list
Task added!
[T][ ] task 22
Now you have 22 tasks in the list
Task added!
[T][ ] task 23
Now you have 23 tasks in the list
Task added!
[T][ ] task 24
Now you have 24 tasks in the list
Task added!
[T][ ] task 25
Now you have 25 tasks in the list
Task added!
[T][ ] task 26
Now you have 26 tasks in the list
Task added!
[T][ ] task 27
Now you have 27 tasks in the list
Task added!
[T][ ] task 28
Now you have 28 tasks in the list
Task added!
[T][ ] task 29
Now you have 29 tasks in the list
Task added!
[T][ ] task 30
Now you have 30 tasks in the list
Task added!
[T][ ] task 31
Now you have 31 tasks in the list
Task added!
[T][ ] task 32
Now you have 32 tasks in the list
Task added!
[T][ ] task 33
Now you have 33 tasks in the list
Task added!
[T][ ] task 34
Now you have 34 tasks in the list
Task added!
[T][ ] task 35
Now you have 35 tasks in the list
Task added!
[T][ ] task 36
Now you have 36 tasks in the list
Task added!
[T][ ] task 37
Now you have 37 tasks in the list
Task added!
[T][ ] task 38
Now you have 38 tasks in the list
Task added!
[T][ ] task 39
Now you have 39 tasks in the list
Task added!
[T][ ] task 40
Now you have 40 tasks in the list
Task added!
[T][ ] task 41
Now you have 41 tasks in the list
Task added!
[T][ ] task 42
Now you have 42 tasks in the list
Task added!
[T][ ] task 43
Now you have 43 tasks in the list
Task added!
[T][ ] task 44
Now you have 44 tasks in the list
Task added!
[T][ ] task 45
Now you have 45 tasks in the list
Task added!
[T][ ] task 46
Now you have 46 tasks in the list
Task added!
[T][ ] task 47
Now you have 47 tasks in the list
Task added!
[T][ ] task 48
Now you have 48 tasks in the list
Task added!
[T][ ] task 49
Now you have 49 tasks in the list
Task added!
[T][ ] task 50
Now you have 50 tasks in the list
Task added!
[T][ ] task 51
Now you have 51 tasks in the list
Task added!
[T][ ] task 52
Now you have 52 tasks in the list
Task added!
[T][ ] task 53
Now you have 53 tasks in the list
Task added!
[T][ ] task 54
Now you have 54 tasks in the list
Task added!
[T][ ] task 55
Now you have 55 tasks in the list
Task added!
[T][ ] task 56
Now you have 56 tasks in the list
Task added!
[T][ ] task 57
Now you have 57 tasks in the list
Task added!
[T][ ] task 58
Now you have 58 tasks in the list
Task added!
[T][ ] task 59
Now you have 59 tasks in the list
Task added!
[T][ ] task 60
Now you have 60 tasks in the list
Task added!
[T][ ] task 61
Now you have 61 tasks in the list
Task added!
[T][ ] task 62
Now you have 62 tasks in the list
Task added!
[T][ ] task 63
Now you have 63 tasks in the list
Task added!
[T][ ] task 64
Now you have 64 tasks in the list
Task added!
[T][ ] task 65
Now you have 65 tasks in the list
Task added!
[T][ ] task 66
Now you have 66 tasks in the list
Task added!
[T][ ] task 67
Now you have 67 tasks in the list
Task added!
[T][ ] task 68
Now you have 68 tasks in the list
Task added!
[T][ ] task 69
Now you have 69 tasks in the list
Task added!
[T][ ] task 70
Now you have 70 tasks in the list
Task added!
[T][ ] task 71
Now you have 71 tasks in the list
Task added!
[T][ ] task 72
Now you have 72 tasks in the list
Task added!
[T][ ] task 73
Now you have 73 tasks in the list
Task added!
[T][ ] task 74
Now you have 74 tasks in the list
Task added!
[T][ ] task 75
Now you have 75 tasks in the list
Task added!
[T][ ] task 76
Now you have 76 tasks in the list
Task added!
[T][ ] task 77
Now you have 77 tasks in the list
Task added!
[T][ ] task 78
Now you have 78 tasks in the list
Task added!
[T][ ] task 79
Now you have 79 tasks in the list
Task added!
[T][ ] task 80
Now you have 80 tasks in the list
Task added!
[T][ ] task 81
Now you have 81 tasks in the list
Task added!
[T][ ] task 82
Now you have 82 tasks in the list
Task added!
[T][ ] task 83
Now you have 83 tasks in the list
Task added!
[T][ ] task 84
Now you have 84 tasks in the list
Task added!
[T][ ] task 85
Now you have 85 tasks in the list
Task added!
[T][ ] task 86
Now you have 86 tasks in the list
Task added!
[T][ ] task 87
Now you have 87 tasks in the list
Task added!
[T][ ] task 88
Now you have 88 tasks in the list
Task added!
[T][ ] task 89
Now you have 89 tasks in the list
Task added!
[T][ ] task 90
Now you have 90 tasks in the list
Task added!
[T][ ] task 91
Now you have 91 tasks in the list
Task added!
[T][ ] task 92
Now you have 92 tasks in the list
Task added!
[T][ ] task 93
Now you have 93 tasks in the list
Task added!
[T][ ] task 94
Now you have 94 tasks in the list
Task added!
[T][ ] task 95
Now you have 95 tasks in the list
Task added!
[T][ ] task 96
Now you have 96 tasks in the list
Task added!
[T][ ] task 97
Now you have 97 tasks in the list
Task added!
[T][ ] task 98
Now you have 98 tasks in the list
Task added!
[T][ ] task 99
Now you have 99 tasks in the list
Task added!
[T][ ] task 100
Now you have 100 tasks in the list
Task list is full (maximum 100 tasks). No task was added.
Here are the tasks in your list:
1.[T][ ] task 1
2.[T][ ] task 2
3.[T][ ] task 3
4.[T][ ] task 4
5.[T][ ] task 5
6.[T][ ] task 6
7.[T][ ] task 7
8.[T][ ] task 8
9.[T][ ] task 9
10.[T][ ] task 10
11.[T][ ] task 11
12.[T][ ] task 12
13.[T][ ] task 13
14.[T][ ] task 14
15.[T][ ] task 15
16.[T][ ] task 16
17.[T][ ] task 17
18.[T][ ] task 18
19.[T][ ] task 19
20.[T][ ] task 20
21.[T][ ] task 21
22.[T][ ] task 22
23.[T][ ] task 23
24.[T][ ] task 24
25.[T][ ] task 25
26.[T][ ] task 26
27.[T][ ] task 27
28.[T][ ] task 28
29.[T][ ] task 29
30.[T][ ] task 30
31.[T][ ] task 31
32.[T][ ] task 32
33.[T][ ] task 33
34.[T][ ] task 34
35.[T][ ] task 35
36.[T][ ] task 36
37.[T][ ] task 37
38.[T][ ] task 38
39.[T][ ] task 39
40.[T][ ] task 40
41.[T][ ] task 41
42.[T][ ] task 42
43.[T][ ] task 43
44.[T][ ] task 44
45.[T][ ] task 45
46.[T][ ] task 46
47.[T][ ] task 47
48.[T][ ] task 48
49.[T][ ] task 49
50.[T][ ] task 50
51.[T][ ] task 51
52.[T][ ] task 52
53.[T][ ] task 53
54.[T][ ] task 54
55.[T][ ] task 55
56.[T][ ] task 56
57.[T][ ] task 57
58.[T][ ] task 58
59.[T][ ] task 59
60.[T][ ] task 60
61.[T][ ] task 61
62.[T][ ] task 62
63.[T][ ] task 63
64.[T][ ] task 64
65.[T][ ] task 65
66.[T][ ] task 66
67.[T][ ] task 67
68.[T][ ] task 68
69.[T][ ] task 69
70.[T][ ] task 70
71.[T][ ] task 71
72.[T][ ] task 72
73.[T][ ] task 73
74.[T][ ] task 74
75.[T][ ] task 75
76.[T][ ] task 76
77.[T][ ] task 77
78.[T][ ] task 78
79.[T][ ] task 79
80.[T][ ] task 80
81.[T][ ] task 81
82.[T][ ] task 82
83.[T][ ] task 83
84.[T][ ] task 84
85.[T][ ] task 85
86.[T][ ] task 86
87.[T][ ] task 87
88.[T][ ] task 88
89.[T][ ] task 89
90.[T][ ] task 90
91.[T][ ] task 91
92.[T][ ] task 92
93.[T][ ] task 93
94.[T][ ] task 94
95.[T][ ] task 95
96.[T][ ] task 96
97.[T][ ] task 97
98.[T][ ] task 98
99.[T][ ] task 99
100.[T][ ] task 100
Bye! Have a nice day!
```

The unexpected-internal-error fallback in `MoistBot.processCommand` has no
user-input sequence that can trigger it: every valid or invalid command is
handled by the specific cases above. It exists as a final safeguard against a
future unchecked implementation failure.

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
