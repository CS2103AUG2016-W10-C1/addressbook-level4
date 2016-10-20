# User Guide


* [About](#about)
* [Quick Start](#quick-start)
* [Features](#features)
* [File Format](#file-format)
* [FAQ](#faq)
* [Command Summary](#command-summary)


## About

TaskMan is a task management application that aims to ease the way you record and search through your tasks and events. It is a desktop application which stores data locally. TaskMan is specifically designed for users who favour working entirely from the keyboard. Instead of navigating through the application with mouse clicks, you simply need to type the right keywords to invoke the necessary commands.

This guide will get you started in just a few steps. It also has detailed 'how to' instructions on each feature of the application and a command summary which can be referred to anytime.


## Quick Start

0. Ensure you have Java version `1.8.0_60` or later installed in your Computer.<br>
   > Having any Java 8 version is not enough. <br>
   This app will not work with earlier versions of Java 8.
   
1. Download the latest `taskman.jar` from the [releases](../../../releases) tab.
2. Copy the file to the folder you want to use as the home folder for your TaskMan.
3. Double-click the file to start the app. The GUI should appear in a few seconds.
   > <img src="images/ui_mockup1.png">

4. Type the command in the command box and press <kbd>Enter</kbd> to execute it. <br>
   e.g. typing **`help`** and pressing <kbd>Enter</kbd> will open the help window.
5. Some example commands you can try:
   * **`list`**: lists all tasks
   * **`add`**` eat vitamins s/fri 1400 for 5 minutes f/2 days t/ribena` :
     adds a task titled `eat vitamins` to TaskMan
   * **`delete`**` 3`: deletes the third task shown in the current list
   * **`exit`**: exits the app
6. Refer to the [Features](#features) section below for details of each command.<br>


## Features

> **Command Format**
> * Words in `UPPER_CASE` are the parameters.
> * Items in `SQUARE_BRACKETS` are optional.
> * Only one item should be picked from items in `CURLY_BRACES`.
> * Items with `...` after them can have multiple instances.
> * The order of parameters is not fixed.

#### Viewing help: `help`
Command Format: `help`

> Help is also shown if you enter an incorrect command e.g. `abcd`
 
#### Adding a task: `add`
Adds a task to TaskMan<br>
Command Format: `add TITLE [d/DEADLINE] [s/SCHEDULE] [f/FREQUENCY] [t/TAG]...`

Parameter | Format
-------- | :-------- 
`SCHEDULE` | `DATETIME`, `DATETIME to DATETIME` or `DATETIME for DURATION` 
`DEADLINE` and `DATETIME` | `[this/next] ddd [hhmm]`
`DURATION` | `<number> <unit of time>`
`FREQUENCY` | See `DURATION`

The `SCHEDULE` represents the period of time the task is scheduled to be worked on.

> Tasks can have any number of tags. Tags may contain spaces and are case-insensitive (i.e. tags "school", "School", and "SCHOOL" are the same tags).

Examples:
* `add learn driving`
* `add cs2101 homework d/next mon 1200`
* `add cs3244 revision s/tue 0000 to tue 0200`
* `add cs2103t tutorial t/java`
* `add eat vitamins s/fri 1400 for 5 minutes f/2 days t/ribena`
* `add backup server s/mon 0800 for 2 hours f/3 months`

#### Adding an event: `add e/`
Adds an event to TaskMan<br>
Command Format: `add e/ TITLE [s/SCHEDULE] [f/FREQUENCY] [t/TAG]...`

Parameter | Format
-------- | :-------- 
`SCHEDULE` | `DATETIME`, `DATETIME to DATETIME` or `DATETIME for DURATION`
`DURATION` | `<number> <unit of time>`
`FREQUENCY` | See `DURATION`

The `SCHEDULE` represents the period of time which the event is occurring.

> Similarly like tasks, events can have any number of tags. Tags may contain spaces and are case-insensitive (i.e. tags "school", "School", and "SCHOOL" are the same tags).

Examples:
* `add e/ CS2103T Lecture s/fri 1400 to fri 1600 f/1wk t/CS2103T`
* `add e/ Project Meeting s/mon 1400 to mon 1400 t/CS2101 t/Lunch Time`

#### Changing the View: `view`
Shows a different view based on the parameters entered.<br>
Command Format: view [{more/less/cal/day}]

Examples:
* `view more`<br>
Shows more details in the list
* `view less`<br>
Shows less details in the list
* `view cal`<br>
Shows a GUI calendar in for the specified month
* `view day`<br>
Shows a detailed view of the entire day, inclusive of tasks and vacant timeblocks 

#### Listing all tasks: `list`
Shows a list of all tasks or events, depending on the view, whose titles contain any of the given keywords or contains any given tags.<br>
Command Format: `list [{e/, all/}] [KEYWORD]... [t/TAG]... `

> * The list returns tasks only by default. Lists with `e/` returns events only while lists with `all/` returns both tasks and events.
> * The search is case-insensitive. e.g `cs3244` will match `CS3244`
> * The order of the keywords does not matter. e.g. `CS3244 Homework` will match `Homework CS3244`
> * Only the title is searched.
> * Only full words will be matched e.g. `CS` will not match `CS3244`
> * Tasks/Events matching at least one keyword or one tag will be returned (i.e. `OR` search).
    e.g. `CS3244` will match `CS3244 Homework`, a task with tags `t/CS2103T` and `t/hw` will match a search for `t/hw`

Examples:
* `list CS2103T`<br>
  Returns Tasks with `CS2103T` in their titles
* `list CS2101 CS3230 CS2103T t/hw`<br>
  Returns any task or event having titles `CS2101`, `CS3230`, or `CS2103T` or tag `hw`

#### Completing a Task: `complete`
Marks the specified task as completed.
Command Format: `complete INDEX` or `complete list`

#### Editing a task/event: `edit`
Edits a task or event to TaskMan<br>
##### For a Task:
Command Format: `edit INDEX TITLE [d/DEADLINE] [s/SCHEDULE] [c/STATUS] [f/FREQUENCY] [t/TAG]...`
##### For an Event:
Command Format: `edit INDEX TITLE [s/SCHEDULE] [f/FREQUENCY] [t/TAG]...`

Parameter | Format
-------- | :-------- 
`SCHEDULE` | `DATETIME`, `DATETIME to DATETIME` or `DATETIME for DURATION` 
`DEADLINE` and `DATETIME` | `[this/next] ddd [hhmm]`
`DURATION` | `<number> <unit of time>`
`FREQUENCY` | See `DURATION`
`STATUS` | `complete/incomplete` or `y/n` where y denotes complete and n denotes incomplete

Fields which are not present are assumed to stay unchanged. By adding tags, previous tags are removed and the new tags are added to the task/event.

Examples:
* `list`<br>
  `edit 1 CS2103T Tutorial s/mon 2200 to tue 0200`<br>
  Changes title of the first task/event to `CS2103T Tutorial` and the schedule to `mon 2200 to tue 0200`
* `edit CS2101 Tutorial d/thu 1159`

#### Deleting a task: `delete`
Deletes the specified task from TaskMan. <!-- Irreversible. But we can undo, LOL. --><br>
Command Format: `delete INDEX` or `delete list`

> Deletes the task at the specified `INDEX` or `list`.
  The index refers to the index number shown in the most recent listing.<br>
  The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list`<br>
  `delete 2`<br>
  Deletes the second task in the TaskMan display.
* `list CS2101`<br>
  `delete list`<br>
  Deletes all of the tasks in the result(s) of the `list` command.

#### Showing all tags: `tag`
Shows all tags used by the user<br>
Command Format: `tag list`

Examples:
* `tag list`<br>
  Outputs: Pen Pineapple Apple P3n

#### Adding tags to tasks: `tag`
Adds tags to the specified task from TaskMan<br>
Command Format: `tag INDEX [t/TAG]...`

Examples:
* `list CS2103T`
  `tag 1 t/V0.1`<br>
  Tags the first task in the result(s) of `list CS2103T` with the tag V0.1.

#### Removing tags from Tasks: `untag`
Removes tags from the specified task from TaskMan
Command Format: `untag INDEX [t/TAG]...` or `untag all`
Examples:
* `list CS2103T`
  `untag 1 t/V0.1`<br>
  Untags the tag V0.1 from the first task in the result(s) of `list CS2103T`.
* `list`
  `untag 1 all`<br>
  Untags all tags from the the first task in list result(s).

#### Editing tag name: `retag`
Edits name of a tag from TaskMan.<br>
Command Format: `retag t/ORIGINAL_NAME t/DESIRED_NAME`

Examples:
* `retag t/CS2103T t/software engine`<br>
  Renames the tag `CS2103T` to `software engine`

#### Sorting tasks: `sort`
Sorts the recent listing of tasks according to the specified attribute. Default sort order is ascending.<br>
Command Format: `sort ATTRIBUTE` [desc]

`ATTRIBUTE` can be any of the fields seen in `add`.

Examples:
* sort title desc
* sort deadline
* sort schedule
* sort frequency desc

#### Viewing command history: `history`
List the 10 most recently executed commands **which have made changes to the data** in reverse chronological order.<br>
Command Format: `history`

#### Undoing commands: `undo`
Undo the X most recently executed commands in the command history. Irreversible. The command history stores a maximum of the 10 most recently executed commands **which have made changes to the data**.<br>
Command Format: `undo [number]` or `undo all`

Examples:
* `undo`<br>
  Undo the most recently executed command in TaskMan.
* `undo 2`<br>
  `undo 3`<br>
  Undo the 5 most recently executed commands in TaskMan.
* `undo all`<br>
  Undo the 10 most recently executed commands in Taskman.

<!--
#### Clearing all entries: `clear`
Clears all entries from TaskMan.<br>
Command Format: `clear`
-->

#### Exiting the program: `exit`
Exits the program.<br>
Command Format: `exit`

#### Saving the data
TaskMan data are saved in the hard disk automatically after any command that changes the data.<br>
There is no need to save manually.

#### Setting the save and load location: `storageloc`
Saves to the specified file name and location and sets the application to load from the specified location in the future.<br>
TaskMan data are saved in a file called tasks.txt in the application folder by default.<br>
The filename **must end in .xml<br>
Format: `storageloc [LOCATION]`

Examples:
* `storageloc C:/Users/Owner/Desktop/new_tasks.xml`<br>
    Sets the new save and load location to C:/Users/Owner/Desktop/new_tasks.xml
* `storageloc default`<br>
    Sets the new save and load location to tasks.xml in the current application folder

## File Format
The file is saved in xml format, which is easy to read and write with appropriate editors.

#### Task
Each Task is saved in the following format:
> `<tasks>`<br>
> `<title>TITLE</title>`<br>
> `<deadline>DD-MM-YYYY TT:TT</deadline>`<br>
> `<schedule>DD-MM-YYYY TT:TT to DD-MM-YYYY TT:TT</schedule>`<br>
> `<frequency>X{hr/d/wk/mth/yr}</frequency>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `</tasks>`<br>

Fields which are empty can be left out.<br>
Example:
> `<tasks>`<br>
> `<title>CS2103T Tutorial HW</title>`<br>
> `<deadline>11-10-2016 23:59</deadline>`<br>
> `<schedule></schedule>`<br>
> `<frequency>1wk</frequency>`<br>
> `<tagged>CS2103T</tagged>`<br>
> `</tasks>`


#### Event
Each Event is saved in the following format:
> `<events>`<br>
> `<title>TITLE</title>`<br>
> `<schedule>DD-MM-YYYY TT:TT to DD-MM-YYYY TT:TT</schedule>`<br>
> `<frequency>1wk</frequency>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `<tagged>TAGNAME</tagged>`<br>
> `</events>`

Fields which are empty can be left out.<br>
Example:
> `<events>`<br>
> `<title>CS2103T Lecture</title>`<br>
> `<schedule>01-10-2016 12:00 to 01-10-2016 14:00</schedule>`<br>
> `<frequency>X{hr/d/wk/mth/yr}</frequency>`<br>
> `<tagged>CS2103T</tagged>`<br>
> `<tagged>lecture</tagged>`<br>
> `</events>`


#### Tag
Each Tag is saved in the following format:
> `<tags>`<br>
> `<tagName>TAGNAME</tagName>`<br>
> `</tags>`

Example:
> `<tags>`<br>
> `<tagName>CS2103T</tagName>`<br>
> `</tags>`

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TaskMan folder.

## Command Summary

Command | Format
-------- | :--------
Add | `add TITLE [d/DEADLINE] [s/SCHEDULE] [f/FREQUENCY] [t/TAG]...    `
Add Event | `add e/ TITLE [s/SCHEDULE] [f/FREQUENCY] [t/TAG]...    `
Clear | `clear`
Complete | `complete INDEX` or `complete list`
Delete | `delete INDEX` or `delete list`
Edit | `edit INDEX TITLE [d/DEADLINE] [s/SCHEDULE] [c/STATUS] [f/FREQUENCY] [t/TAG]...`
Exit | `exit`
Help | `help`
History | `history`
List | `list [{e/, all/}] [KEYWORD]... [t/TAG]...`
Retag | `retag t/ORIGINAL t/DESIRED`
Sort | `sort ATTRIBUTE [desc]`
Storageloc | `storageloc [LOCATION]` or `storageloc default`
Tag List | `tag list`
Tag | `tag INDEX [t/TAG]...`
Undo | `undo [NUMBER]`
Untag | `untag INDEX [t/TAG]...` or `untag all`
View | `view` or `view PARAM`