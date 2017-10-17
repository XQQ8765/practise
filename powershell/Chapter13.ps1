## Chapter13 in book "Windows PowerShell Cookbook"
## Chapter13. Internet-Enabled Scripts

##13.1. Read a Line of User Input
$directory = Read-Host "Enter a directory name"
$directory


##13.2. Read a Key of User Input
$key = [Console]::ReadKey($true)
$key

$key = $host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
$key


##13.3. Program: Display a Menu to the User
$caption = "Please specify a task"
$message = "Specify a task to run"
$option = "&Clean Temporary Files","&Defragment Hard Drive"
$helptext = "Clean the temporary files from the computer", "Run the defragment task"
$default = 1
./Read-HostWithPrompt.ps1 $caption $message $option $helptext $default


##13.4. Display Messages and Output to the User
function Get-Information
{
	"Hello World"
	Write-Output (1 + 1)
}
Get-Information
$result = Get-Information
$result[1]

function Get-DirectorySize
{
	$size = (Get-ChildItem | Measure-Object -Sum Length).Sum
	Write-Host ("Directory size: {0:N0} bytes" -f $size)
}
Get-DirectorySize


function Get-DirectorySize
{
	Write-Debug "Current Directory: $(Get-Location)"
	Write-Verbose "Getting size"
	$size = (Get-ChildItem | Measure-Object -Sum Length).Sum
	Write-Verbose "Got size: $size"
	Write-Host ("Directory size: {0:N0} bytes" -f $size)
}
$DebugPreference = "Continue"
Get-DirectorySize

$DebugPreference = "SilentlyContinue"
$VerbosePreference = "Continue"
Get-DirectorySize


##Example 13-3. An error message caused by formatting statements
function Get-ChildItemSortedByLength($path = (Get-Location))
{
	Get-ChildItem $path | Format-Table | Sort Length
}
Get-ChildItemSortedByLength


##Example 13-4. A function that does not generate formatting errors
## Get the list of items in a directory, sorted by length
function Get-ChildItemSortedByLength($path = (Get-Location))
{
	## Problematic version
	#Get-ChildItem $path | Format-Table | Sort Length
	## Fixed version
	Get-ChildItem $path | Sort Length | Format-Table
}
Get-ChildItemSortedByLength


##13.5. Provide Progress Updates on Long-Running Tasks
./Invoke-LongRunningOperation.ps1


##13.6. Write Culture-Aware Scripts


##13.7. Support Other Languages in Script Output
##Example 13-6. Importing culture-specific strings for a script or module
## Create some default messages for English cultures, and
## when culture-specific messages are not available.
$messages = DATA {
	@{
		Greeting = "Hello, {0}"
		Goodbye = "So long."
	}
}
## Import localized messages for the current culture.
Import-LocalizedData messages -ErrorAction SilentlyContinue
## Output the localized messages
$messages.Greeting -f "World"
$messages.Goodbye


## 13.8. Program: Invoke a Script Block with Alternate Culture Settings
./Use-Culture.ps1 fr-FR { Get-Date -Date "25/12/2007" }


##13.9. Access Features of the Host¡¯s User Interface
$host.UI | Get-Member | Select Name,MemberType | Format-Table -Auto
$host.UI.RawUI | Get-Member | Select Name,MemberType | Format-Table -Auto


##13.10. Program: Add a Graphical User Interface to Your Script
dir | ./Select-GraphicalFilteredObject.ps1


##13.11. Interact with MTA Objects
.\Invoke-ScriptThatRequiresMta.ps1 Test1 Test2
