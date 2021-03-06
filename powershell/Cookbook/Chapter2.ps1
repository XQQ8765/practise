## Chapter2 in book "Windows PowerShell Cookbook"

##Where-Object
Get-Process | Where-Object { $_.Name -like "*Search*" }
Get-Process | Where-Object { -not $_.Responding }
Get-Service | Where-Object { $_.Status -eq "Stopped" }
Get-Process | Where-Object Name -like "*Search*"

##Group and Pivot data
##Group-Object
$h = dir | group -AsHash -AsString Length
$h
#Create a hashtable to replace use "Where-Object" repeatly
$processes = Get-Process | Group-Object -AsHash Id
$processes[1216]
$processes[1212]


##Simplify Where-Object Filters
Get-Process | Where-Object { $_.Handles -gt 1000 }
Get-Process | Where-Object Handles -gt 1000
Get-Process | ? HasExited

##Work with Each item in a List or Command Output
1..10 | Foreach-Object { $_ * 2 }
Get-ChildItem *.txt | Foreach-Object { attrib -r $_ }

$notepadProcesses = Get-Process notepad
$notepadProcesses | Foreach-Object { $_.WaitForExit() }

$myArray = 1,2,3,4,5
$myArray | Foreach-Object -Begin {$sum = 0 } -Process { $sum += $_ } -End { $sum }
$myArray | Foreach-Object { $sum = 0 } { $sum += $_ } { $sum }

##Simplify Most Foreach-Object Pipelines
Get-Process | Foreach-Object { $_.Name }
Get-Process | Foreach-Object Name
Get-Process | % Name | % ToUpper


"Hello","World" | Invoke-Member Length
"Hello","World" | Invoke-Member -m ToUpper
#Shorter alias
Set-Alias :: Invoke-Member
"Hello","World" | :: Length
