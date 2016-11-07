# A0136070Runused
###### /UserGuide.md
``` md
<!--
#### Adding tags to tasks: `tag`
Adds tags to the specified task from TaskMan<br>
Command Format: `tag INDEX [t/TAG]...`

Examples:
* `list programming`
  `tag 1 t/V0.1`<br>
  Tags the first task in the result(s) of `list programming` with the tag V0.1.

#### Removing tags from Tasks: `untag`
Removes tags from the specified task from TaskMan
Command Format: `untag INDEX [t/TAG]...` or `untag all`
Examples:
* `list programming`
  `untag 1 t/V0.1`<br>
  Untags the tag V0.1 from the first task in the result(s) of `list programming`.
* `list`
  `untag 1 all`<br>
  Untags all tags from the the first task in list result(s).

#### Editing tag name: `retag`
Edits name of a tag from TaskMan.<br>
Command Format: `retag t/ORIGINAL_NAME t/DESIRED_NAME`

Examples:
* `retag t/programming t/software engine`<br>
  Renames the tag `programming` to `software engine`

#### Sorting tasks: `sort`
Sorts the recent listing of tasks according to the specified attribute. Default sort order is ascending.<br>
Command Format: `sort ATTRIBUTE` [desc]

`ATTRIBUTE` can be any of the fields seen in `add`.

Examples:
* sort title desc
* sort deadline
* sort schedule
-->
```
