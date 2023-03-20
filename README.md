## Coding Styles
> 1. **Use spaces to indent** instead of tabs or set tab to 4 spaces. This is because different IDEs may have assigned different numbers of spaces to tabs and may cause inconsistencies in indentation.
> 2. **Put spaces around operators.** For instances, ```int a = b * (c + d);``` Note that there are spaces around * and + but not the parentheses because the parentheses are not operators.
> 3. When defining a function, **put the opening bracket in the same line as the header of the function.** For example,
```
public int getSum(int a, int b) {
	return a + b;
}
Note: “{“ is in the same line as the header
```
> 4. **Avoid having a long chain of function calls.** For example, ```a.b().c().d().e()```. Instead, split this up and divide into distinct variables. For example,
```
statement_one = a.b()
statement_two = statement_one.c().d()
```
> 5. When naming variables, try to **use meaningful names.** For variable names that contain several words, use upper camel naming convention. For example, ```firstDataPoint```.
> 6. Avoid over-commenting. Only comment when you feel necessary.
> 7. Braces are used with if, else, for, do and while statements, **even when the body is empty or contains only a single statement**.
> 8. Only **write one statement a line**. For example, ``` int a = 1; int c = 30;``` should be written in two separate lines.
> 9. Variable Declarations: <br>
	a.**One variable per declaration** <br>
	   Every variable declaration (field or local) declares only one variable: declarations such as ```int a, b;``` are not used. <br>
	   Exception: Multiple variable declarations are acceptable in the header of a for loop. <br> <br>
	b. **Declared when needed** <br>
	   Local variables are not habitually declared at the start of their containing block or block-like construct. Instead, local variables are declared close to the point they are first used (within reason), to minimize their scope. Local variable declarations typically have initializers, or are initialized immediately after declaration.

## Git Operations
> 1. ```git checkout -b <branch_name>```: Create a new branch. <br>***Note**: Please create a new branch when modifying the code in the repository. Once done editing, push your code to your own branch on github first.<br> **Alert: Not the main branch!!!** <br>Then, there will be a **"Compare&Make Pull Request"** option available. Make a pull request with a detailed description about what changes you make.*
> 2. ```git checkout <branch_name>```: This allows to switch between branches.
> 3. ```git add <file_name>```: This notifies GIT of the changes that you made.
> 4. ```git commit -m <message>```: This creates a GIT commit with an associated message
> 5. ```git push```: This pushes the changes onto GitHub.
<br>***Note**: Use ```git push -u origin <branch_name>``` to push to your own branch.*
> 6. ```git pull```: Update your local machine code to match GitHub.
```
# This creates a new branch called "arthur-branch" where you can modify the code without affecting the main branch
>> git checkout -b arthur-branch
# This switches to arthur-branch
>> git checkout arthur-branch
# Notify GIT about changes in AddEventTag.java file
>> git add AddEventTag.java
>> git commit -m "Create the Tag type"
# This pushes the commits to arthur-branch
>> git push -u origin arthur-branch
```
