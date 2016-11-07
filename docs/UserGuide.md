<!--@@author A0136070R-->
# User Guide


* [About](#about)
* [Quick Start](#quick-start)
* [Features](#features)
* [File Format](#file-format)
* [FAQ](#faq)
* [Command Summary](#command-summary)


## About

TaskMan is a task management application which stores data locally. It aims to ease the way you record and search through your daily activities.

TaskMan is specifically designed for users who favour working entirely from the keyboard. Instead of navigating through the application with mouse clicks, you simply need to type the necessary commands and shortcuts.

This guide will get you started in just a few steps. It also has detailed 'how to' instructions on each feature of the application and a command summary which can be referred to anytime.

## Quick Start

0. Ensure you have Java version `1.8.0_60` or later installed in your Computer.<br>
   > Having any Java 8 version is not enough. <br>
   This app will not work with earlier versions of Java 8.
   
1. Download the latest `taskman.jar` from the [releases](../../../releases) tab.
2. Copy the file to the folder you want to use as the home folder for your TaskMan.
3. Double-click the file to start the app. The GUI should appear in a few seconds.
   > <img src="images/UI.png">

4. Type the command in the command box and press <kbd>Enter</kbd> to execute it. <br>
   e.g. typing **`help`** and pressing <kbd>Enter</kbd> will open the help window.
5. Some example commands you can try:
   * **`list`**: lists all tasks
   * **`add`**` eat vitamins s/fri 1400 for 5 minutes f/2 days t/ribena` :
     adds a task titled `eat vitamins` to TaskMan
   * **`delete`**` d3`: deletes the third task shown in the deadline list
   * **`exit`**: exits the app
6. Refer to the [Features](#features) section below for details of each command.<br>


## Features

>**Command Format**
> * Words in `UPPER_CASE` are the parameters.
> * Items in `SQUARE_BRACKETS` are optional.
> * Only one item should be picked from items in `CURLY_BRACES`.
> * Items with `...` after them can have multiple instances.

Parameter | Format
-------- | :-------- 
`TITLE` | Any sequence of alphanumeric characters
`SCHEDULE` | `DATETIME, DATETIME`, `DATETIME to DATETIME` or `DATETIME for DURATION` 
`DEADLINE` and `DATETIME` | `[this/next] ddd [hhmm]`
`DURATION` | `<number> <unit of time>`
`TAG` | Can contain spaces and are case-insensitive
`STATUS` | `complete/incomplete` or `y/n` where y denotes complete and n denotes incomplete
`NUMBER` | integer from 1 to 10


#### Viewing help: `help`
View command formats.
Command Format: `help`

> * Typing help or pressing F1 displays a list of commands available in TaskMan, along with their description and respective command formats.
> * Help is also shown if you enter an incorrect command e.g. abcd.
> * You may navigate down the help window via the up and down arrow keys, and to exit the help window and return to TaskMan, simply press ESC.

#### Adding a task: `add`
Adds a task to TaskMan<br>
Command Format: `add TITLE [d/DEADLINE] [s/SCHEDULE] [t/TAG]...`

The `SCHEDULE` represents the period of time the task is scheduled to be worked on. Tasks can have any number of `TAG`s.

Examples:
* `add learn driving`
* `add cs2101 homework d/next mon 1200`
* `add math revision s/today 8pm to mon 2359`
* `add software engineering tutorial t/java`
* `add eat magic pill d/sat 12am s/fri 2pm for 5 minutes t/ribena`
* `add backup computer s/mon 8am, mon 11am t/work`
* `add buy gift for dad d/17 dec 2016 23:59 t/birthday t/cash`
<!--@@author-->

<!--@@author A0140136W-->
#### Adding an event: `adde`
Adds an event to TaskMan<br>
Command Format: `adde TITLE s/SCHEDULE [t/TAG]...`

The event must have a `SCHEDULE`, and can contain any number of `TAG`s (i.e. categories).The `SCHEDULE` represents the period of time which the event is occurring.

Examples:
* `adde dance lesson s/tomorrow 1pm to tomorrow 3pm`
* `adde driving test s/next Tue for 2 hours t/important`
<!--@@author-->


#### Listing all tasks: `list`
Shows a list of all activities whose titles contain any of the given keywords or contains any given tags.<br>
Command Format: `list [{s, d, f}] [KEYWORD]... [t/TAG]... `

* List called with an empty argument displays all activities.
* List with `KEYWORD` or `TAG`s filters all 3 panels in TaskMan
* List can be called with the paramters `{s, d, f}` to filter specific panels 
<br>

> * The search is case-insensitive. e.g `cs3244` will match `CS3244`
> * The order of the keywords does not matter. e.g. `CS3244 Homework` will match `Homework CS3244`
> * Only full words will be matched e.g. `CS` will not match `CS3244`
> * Tasks/Events matching at least one keyword or one tag will be returned (i.e. `OR` search).
    e.g. `CS3244` will match `CS3244 Homework`, a task with tags `t/CS2103T` and `t/hw` will match a search for `t/hw`

Examples:
* `list`<br>
  Lists all tasks without any filter
* `list interview`<br>
  Lists all tasks with the word `interview` in their titles
* `list t/school`<br>
  Lists all tasks with the tag `school`
* `list eat alex`<br>
  Lists all tasks with the word `eat`, `alex`, or both in their titles.
* `list t/boyfriend t/date`<br>
  Lists all tasks with the tags `boyfriend` or `date`
* `list buy t/important`<br>
  Lists all tasks with the word `buy` in their titles and with the tag `important`

<!--@@author A0140136W-->
#### Selecting a Task: `select`
Display more details of the specified task.
Command Format: `select INDEX`
<!--@@author-->

<!--@@author A0139019E-->
#### Editing a task/event: `edit`
Edits an activity in TaskMan<br>
##### For a Task:
Command Format: `edit INDEX TITLE [d/DEADLINE] [s/SCHEDULE] [c/STATUS] [t/TAG]...`
##### For an Event:
Command Format: `edit INDEX TITLE [s/SCHEDULE] [t/TAG]...`


Fields which are not present are assumed to stay unchanged. By adding tags, previous tags are removed and the new tags are added to the activity.

Examples:
* `list`<br>
  `edit s7 swallow magic pill s/friday 2pm for 30 minutes`<br>
* `edit d2 tell Alex to study for exam d/next fri 9am`
* `edit f1 master driving d/12 saturdays from now`
* `edit d1 d/8pm`

#### Completing a Task: `complete`
Marks the specified task as completed.
Command Format: `complete INDEX`

#### Deleting a task: `delete`
Deletes the specified task from TaskMan.<br>
Command Format: `delete INDEX`

> Deletes the task at the specified `INDEX`.
  The index refers to the index number shown in the most recent listing.<br>
  The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list`<br>
  `delete f4`<br>
  Deletes the fourth activity in the Floating panel.
* `list CS2101`<br>
  `delete d9`<br>
  Deletes the ninth activity in the Deadline panel.

<!--@@author-->
#### Showing all tags: `tags`
Shows all tags used by the user<br>
Command Format: `tags`

Examples:
* `tags`<br>
  Outputs: `[Apple] [Pen] [Pineapple] [P3n]`

#### Viewing command history: `history`
List the 10 most recently executed commands **which have made changes to the data** in reverse chronological order.<br>
Command Format: `history`

#### Undoing commands: `undo`
Undo the `NUMBER` most recently executed commands in the command history. Irreversible. The command history stores a maximum of the 10 most recently executed commands **which have made changes to the data**.<br>
Command Format: `undo [NUMBER]`

Examples:
* `undo`<br>
  Undo the most recently executed command in TaskMan.
* `undo 2`<br>
  `undo 3`<br>
  Undo the 5 most recently executed commands in TaskMan.


#### Clearing all entries: `clear`
Clears all entries from TaskMan.<br>
Command Format: `clear`


#### Exiting the program: `exit`
Exits the program.<br>
Command Format: `exit`

#### Saving the data
TaskMan data are saved in the hard disk automatically after any command that changes the data.<br>
There is no need to save manually.

<!--@@author A0121299A-->
#### Setting the save and load location: `storageloc`
Saves to the specified file name and location and sets the application to load from the specified location in the future.<br>
The command can be used to save the data to the default location or to view the current storage location.<br>
TaskMan data are saved in data/taskMan.txt in the application folder by default.<br>

Format: `storageloc [LOCATION]`

Examples:
* `storageloc C:/Users/Owner/Desktop/new_tasks.xml`<br>
    Sets the new save and load location to C:/Users/Owner/Desktop/new_tasks.xml
* `storageloc default`<br>
    Sets the new save and load location to tasks.xml in the current application folder
* `storageloc view`<br>
    Displays the current storage location

## File Format
The file is saved in xml format, which is easy to read and write with appropriate editors.

#### Task
Each Task is saved in the following format:
> `<tasks>`<br>
> `<title>TITLE</title>`<br>
> `<deadline>DD-MM-YYYY TT:TT</deadline>`<br>
> `<scheduleStart>DD-MM-YYYY TT:TT</scheduleStart>`<br>
> `<scheduleEnd>DD-MM-YYYY TT:TT</scheduleEnd>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `</tasks>`<br>

Fields which are empty can be left out.<br>
Example:
> `<tasks>`<br>
> `<title>CS2103T Tutorial HW</title>`<br>
> `<deadline>11-10-2016 23:59</deadline>`<br>
> `<tagged>CS2103T</tagged>`<br>
> `</tasks>`

#### Event
Each Event is saved in the following format:
> `<event>`<br>
> `<title>TITLE</title>`<br>
> `<scheduleStart>DD-MM-YYYY TT:TT</scheduleStart>`<br>
> `<scheduleEnd>DD-MM-YYYY TT:TT</scheduleEnd>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `</event>`

Fields which are empty can be left out.<br>
Example:
> `<event>`<br>
> `<title>CS2103T Lecture</title>`<br>
> `<scheduleStart>01-10-2016 12:00</scheduleStart>`<br>
> `<scheduleEnd01-10-2016 14:00</scheduleEnd>`<br>
> `<tagged>CS2103T</tagged>`<br>
> `</event>`

<!--@@author -->
## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TaskMan folder.

## Command Summary

Command | Format
-------- | :--------
Add | `add TITLE [d/DEADLINE] [s/SCHEDULE] [t/TAG]...    `
Adde | `adde TITLE s/SCHEDULE [t/TAG]...    `
Clear | `clear`
Complete | `complete INDEX`
Delete | `delete INDEX`
Edit | `edit INDEX TITLE [d/DEADLINE] [s/SCHEDULE] [c/STATUS] [t/TAG]...`
Exit | `exit`
Help | `help`
History | `history`
List | `list [{s, d, f}] [KEYWORD]... [t/TAG]...`
Select | `select INDEX`
Storageloc | `storageloc [LOCATION]` or `storageloc default` or `storageloc view`
Tags | `tags`
Undo | `undo [NUMBER]`
