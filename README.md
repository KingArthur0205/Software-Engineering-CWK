## Git Operations
> 1. ```git checkout -b <branch_name>```: Create a new branch. <br>***Note**: Please create a new branch when modifying the code in the repository. Once done editing, push your code to your own branch on github first.<br> **Alter: Not the main branch!!!** <br>Then, there will be a **"Compare&Make Pull Request"** option available. Make a pull request with a detailed description about what changes you make.*
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
>> get checkout arthur-branch
# Notify GIT about changes in AddEventTag.java file
>> git add AddEventTag.java
>> git commit -m "Create the Tag type"
# This pushes the commits to arthur-branch
>> git push -u origin arthur-branch
```
