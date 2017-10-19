## Chapter14 in book "Windows PowerShell Cookbook"
## Chapter14. Debugging


##14.1. Prevent Common Scripting Errors
function BuggyFunction
{
	$testVariable = "Hello"
	if($testVariab1e -eq "Hello")
	{
		"Should get here"
	}
	else
	{
		"Should not get here"
	}
}
BuggyFunction
Set-StrictMode -Version Latest
BuggyFunction


$testVariable = "Hello"
$tsetVariable += " World"
$testVariable

Remove-Item Variable:\tsetvariable
Set-StrictMode -Version Latest
$testVariable = "Hello"
$tsetVariable += " World"


##14.2. Trace Script Execution
function BuggyFunction
{
	$testVariable = "Hello"
	if($testVariab1e -eq "Hello")
	{
		"Should get here"
	}
	else
	{
		"Should not get here"
	}
}
Set-PsDebug -Trace 1
BuggyFunction


$debugPreference = "Continue"
./Invoke-ComplexScript.ps1


##14.3. Set a Script Breakpoint
Set-PsBreakPoint .\Invoke-ComplexDebuggerScript.ps1 -Line 21
Set-PSBreakpoint -Command Get-ChildItem
Set-PsBreakPoint -Variable dirCount


##14.4. Debug a Script When It Encounters an Error
./Enable-BreakOnError.ps1
1/0
$error


##14.5. Create a Conditional Breakpoint
Set-PsBreakpoint .\looper.ps1 -Line 3 -Action {
	if($count -eq 4) { break }
}


##14.6. Investigate System State While Debugging


##14.7. Program: Watch an Expression for Changes

...


