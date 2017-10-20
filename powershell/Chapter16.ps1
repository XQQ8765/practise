## Chapter16 in book "Windows PowerShell Cookbook"
## Chapter16. Environmental Awareness


##16.1. View and Modify Environment Variables
$env:Username

$env:PATH = $env:PATH + ";.;"
Invoke-DemonstrationScript

#PowerShell defines an env: drive (much like c: or d:) that provides access to environment variables
dir env:
Get-Content Env:\Username
Get-Content Env:Username
$env:Username

[Environment]::GetEnvironmentVariable("Path", "User")

$pathElements = @([Environment]::GetEnvironmentVariable("Path", "User") -split ";")
$pathElements += "d:\tools"
$newPath = $pathElements -join ";"
[Environment]::SetEnvironmentVariable("Path", $newPath, "User")


##16.2. Modify the User or System Path
$scope = "User"
$pathElements = @([Environment]::GetEnvironmentVariable("Path", $scope) -split ";")
$pathElements += "d:\tools"
$newPath = $pathElements -join ";"
[Environment]::SetEnvironmentVariable("Path", $newPath, $scope)


##16.3. Access Information About Your Command's Invocation
.\Get-ScriptInfo.ps1


##16.4. Program: Investigate the InvocationInfo Variable
.\Get-InvocationInfo.ps1


##16.5. Find Your Script¡¯s Name
# If you want to know just the name of the script (rather than its full path), use the Split-Path cmdlet:
$scriptName = Split-Path -Leaf $PSCommandPath

function Get-ScriptName
{
	$myInvocation.ScriptName
}
$scriptName = & { $myInvocation.ScriptName }

#If you are in the body of a script, you can directly get the name of the current script by typing:
$myInvocation.Path

#If you are in a function or script block, though, you must use:
$myInvocation.ScriptName


##16.6. Find Your Script¡¯s Location
#To determine the location of the currently executing script, use the $PSScriptRoot variable. 
#For example, to load a data file from the same location as your script:
$dataPath = Join-Path $PSScriptRoot data.clixml

#Or to run a command from the same location as your script:
$helperUtility = Join-Path $PSScriptRoot helper.exe
& $helperUtility

#In PowerShell version 2
function Get-ScriptPath
{
	Split-Path $myInvocation.ScriptName
}


##16.7. Find the Location of Common System Paths
#To determine the location of common system paths and special folders
[Environment]::GetFolderPath("System")

#For paths not supported by this method (such as All Users Start Menu), use the WScript.Shell COM object
$shell = New-Object -Com WScript.Shell
$allStartMenu = $shell.SpecialFolders.Item("AllUsersStartMenu")

#Example 16-2. Folders supported by the [Environment]::GetFolderPath() method
[Enum]::GetValues([Environment+SpecialFolder])


#Example 16-3. Differences between folders supported by [Environment]::GetFolder-Path() and the WScript.Shell COM object
$shell = New-Object -Com WScript.Shell
$shellPaths = $shell.SpecialFolders | Sort-Object

$netFolders = [Enum]::GetValues([Environment+SpecialFolder])
$netPaths = $netFolders | Foreach-Object { [Environment]::GetFolderPath($_) } | Sort-Object

## See the shell-only paths
Compare-Object $shellPaths $netPaths | Where-Object { $_.SideIndicator -eq "<=" }
## See the .NET-only paths
Compare-Object $shellPaths $netPaths | Where-Object { $_.SideIndicator -eq "=>" }


##16.8. Get the Current Location
Get-Location
$pwd

## One problem that sometimes impacts scripts that work with the .NET Framework is that PowerShell¡¯s concept of ¡°current location¡± isn¡¯t always the same as the Power-Shell.exe process¡¯s ¡°current directory.¡±
Get-Process | Export-CliXml processes.xml
$reader = New-Object Xml.XmlTextReader processes.xml
$reader.BaseURI
#Output is: file:///C:/Users/rxiao/processes.xml

Get-Process | Export-CliXml processes.xml
$reader = New-Object Xml.XmlTextReader (Resolve-Path processes.xml)
$reader.BaseURI
#Output is: file:///D:/workspace/practise/powershell/processes.xml


##16.9. Safely Build File Paths Out of Their Components
#To join elements of a path together, use the Join-Path cmdlet:
Join-Path (Get-Location) newfile.txt


##16.10. Interact with PowerShell¡¯s Global Environment
#To make a variable available to the entire PowerShell session, use a $GLOBAL: prefix when you store information in that variable:
## Create the web service cache, if it doesn't already exist
if(-not (Test-Path Variable:\Lee.Holmes.WebServiceCache))
{
	${GLOBAL:Lee.Holmes.WebServiceCache} = @{}
}


##16.11. Determine PowerShell Version Information
$psVersionTable


##16.12. Test for Administrative Privileges
$identity = [System.Security.Principal.WindowsIdentity]::GetCurrent()
$principal = [System.Security.Principal.WindowsPrincipal] $identity
$role = [System.Security.Principal.WindowsBuiltInRole] "Administrator"
if(-not $principal.IsInRole($role))
{
	throw "This script must be run from an elevated shell."
}

$principal.IsInRole


