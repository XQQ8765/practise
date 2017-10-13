## Chapter11 in book "Windows PowerShell Cookbook"
## Chapter11. Code Reuse

##11.1. Write a Script
##11.2. Write a Function


##11.3. Find a Verb Appropriate for a Command Name
PS > Get-Verb In* | Format-Table -Auto


##11.4. Write a Script Block

##11.5. Return Data from a Script, Function, or Script Block
function GetDate
{
    Get-Date
}
$tomorrow = (GetDate).AddDays(1)
$tomorrow

##11.6. Package Common Commands in a Module
Import-Module .\Temperature.psm1 -Verbose
Convert-CelciusToFahrenheit 18
Convert-FahrenheitToCelcius 18


##11.7. Write Commands That Maintain State
Import-Module ./PersistentState -Verbose
Get-Process -Name powershell_ise | remember
recall


##11.8. Selectively Export Commands from a Module


##11.9. Diagnose and Interact with Internal Module State
Import-Module ./PersistentState -Verbose
Get-Module PersistentState
"Hello World" | Set-Memory
$m = Get-Module PersistentState
./Enter-Module $m


#interact with internal state of a specific module for diagnostic purposes
$m = Get-Module PersistentState
& $m { dir variable:\mem* }


##11.10. Handle Cleanup Tasks When a Module Is Removed
PS > Import-Module ./TidyModule
PS > $TidyModuleStatus
Initialized
PS > Remove-Module TidyModule
PS > $TidyModuleStatus
Cleaned Up


##11.11. Access Arguments of a Script, Function, or Script Block
.\Get-Arguments.ps1 a 1


#Supporting PowerShell’s common parameters
#Declaring an advanced function
function Invoke-MyAdvancedFunction
{
    [CmdletBinding()]#To get these additional parameters
    param()
    Write-Verbose "Verbose Message"
}

Invoke-MyAdvancedFunction
Invoke-MyAdvancedFunction -Verbose

#Using the $args array
function Reverse
{
    $argsEnd = $args.Length - 1
    $args[$argsEnd..0]
}
Reverse 1 2 3 4

#If you have defined parameters in your script, the $args array represents any arguments not captured by those parameters
function MyParamsAndArgs {
    param($MyArgument)
    "Got MyArgument: $MyArgument"
    "Got Args: $args"
}
MyParamsAndArgs -MyArgument One Two Three


##11.12. Add Validation to Parameters


##11.13. Accept Script Block Parameters with Local Variables
$name = "Hello There"
.\Invoke-ScriptBlockClosure.ps1 { $name }


##11.14. Dynamically Compose Command Parameters
$parameters = @{
    Name = "PowerShell";
    WhatIf = $true
}
Stop-Process @parameters


function Stop-ProcessWhatIf($name)
{
    Stop-Process -Name $name -Whatif
}
Stop-ProcessWhatIf PowerShell


##11.15. Provide -WhatIf, -Confirm, and Other Cmdlet Features
function Invoke-MyAdvancedFunction
{
    [CmdletBinding(SupportsShouldProcess = $true)]
    param()
    if($psCmdlet.ShouldProcess("test.txt", "Remove Item"))
    {
        "Removing test.txt"
    }
    Write-Verbose "Verbose Message"
}
Invoke-MyAdvancedFunction -WhatIf


function Invoke-MyDangerousFunction
{
    [CmdletBinding()]
param(
    [Switch] $Force
)
if($Force -or $psCmdlet.ShouldContinue(
    "Do you wish to invoke this dangerous operation?
    Changes can not be undone.",
    "Invoke dangerous action?"))
    {
        "Invoking dangerous action"
    }
}
Invoke-MyDangerousFunction
Invoke-MyDangerousFunction -Force


./Invoke-AdvancedFunction.ps1 { $host.EnterNestedPrompt() }
$psCmdlet | Get-Member


##11.16. Add Help to Scripts or Functions


##11.17. Add Custom Tags to a Function or Script Block
function TestFunction
{
    [System.ComponentModel.Description("Information I care about")]
    param()
    "Some function with metadata"
}
$testFunction = Get-Command TestFunction
$testFunction.ScriptBlock.Attributes


function Get-CommandForContext($context)
{
    Get-Command -CommandType Function |
    Where-Object { $_.ScriptBlock.Attributes |
    Where-Object { $_.Description -eq "Context=$context" } }
}

function WebsiteFunction
{
    [System.ComponentModel.Description("Context=Website")]
    param()
    "Some function I use with my website"
}

function ExchangeFunction
{
    [System.ComponentModel.Description("Context=Exchange")]
    param()
    "Some function I use with Exchange"
}

Get-CommandForContext Website
Get-CommandForContext Exchange


##Example 11-16. Finding all useful attributes
##find all attributes that have a constructor that takes a single string as its
argument
$types = [Appdomain]::CurrentDomain.GetAssemblies() | Foreach-Object { $_.GetTypes() }
foreach($type in $types)
{
    if($type.BaseType -eq [System.Attribute])
    {
        foreach($constructor in $type.GetConstructors())
        {
            if($constructor.ToString() -match "\(System.String\)")
            {
                $type
            }
        }
    }
}


##11.18. Access Pipeline Input
