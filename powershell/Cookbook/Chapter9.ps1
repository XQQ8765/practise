## Chapter9 in book "Windows PowerShell Cookbook"
## Chapter9. Simple Files

##9.1. Get the Content of a File
PS > $content = Get-Content c:\temp\file.txt
PS > $content = Get-Content c:\temp\file.txt
PS > $content = Get-Content c:\temp\file.txt -Raw
PS > $contentLines = [System.IO.File]::ReadAllLines("c:\temp\file.txt")


##9.2. Search a File for Text or a Pattern
#To search a file for an exact (but case-insensitive) match
PS > Select-String -Simple SearchText file.txt

#To search a file for a regular expression
PS > Select-String "\(...\) ...-...." phone.txt

#To recursively search all *.txt files for a regular expression, pipe the results of GetChildItem to the Select-String cmdlet
PS > Get-ChildItem *.txt -Recurse | Select-String pattern
PS > dir *.txt -rec | sls pattern


##9.3. Parse and Manage Text-Based Logfiles
#Example 9-1. Getting a list of files modified by hotfixes
PS > cd $env:WINDIR
PS > $parseExpression = "(.*): Destination:(.*) \((.*)\)"
PS > $files = dir kb*.log -Exclude *uninst.log
PS > $logContent = $files | Get-Content | Select-String $parseExpression
PS > $logContent

PS > $properties = "Time","File","FileVersion"
PS > $logObjects = $logContent | Convert-TextObject -ParseExpression $parseExpression -PropertyName $properties


##9.4. Parse and Manage Binary Files


##9.5. Create a Temporary File
$filename = [System.IO.Path]::GetTempFileName()
Remove-Item -Force $filename


##9.6. Search and Replace Text in a File
#Replacing text in a file
PS > $filename = "file.txt"
PS > $match = "source text"
PS > $replacement = "replacement text"
PS >
PS > $content = Get-Content $filename
PS > $content
This is some source text that we want
to replace. One of the things you may need
to be careful about with Source
Text is when it spans multiple lines,
and may have different Source Text
capitalization.
PS >
PS > $content = $content -creplace $match,$replacement
PS > $content
This is some replacement text that we want
to replace. One of the things you may need
to be careful about with Source
Text is when it spans multiple lines,
and may have different Source Text
capitalization.
PS > $content | Set-Content $filename

#Work with files encoded in Unicode or another (OEM) code page
$content | Out-File -Encoding Unicode $filename
$content | Out-File -Encoding OEM $filename

#Replace text using a pattern instead of plain text
PS > $content = Get-Content names.txt
PS > $content
John Doe
Mary Smith
PS > $content -replace '(.*) (.*)','$2, $1'
Doe, John
Smith, Mary

#Replace text that spans multiple lines
$singleLine = Get-Content file.txt -Raw
$content = $singleLine -creplace "(?s)Source(\s*)Text",'Replacement$1Text'

#Replace text in large files
$filename = "file.txt"
$temporaryFile = [System.IO.Path]::GetTempFileName()
$match = "source text"
$replacement = "replacement text"
Get-Content $filename | Foreach-Object { $_ -creplace $match,$replacement } | Add-Content $temporaryFile
Remove-Item $filename
Move-Item $temporaryFile $filename


##9.8. Program: View the Hexadecimal Representation of Content





