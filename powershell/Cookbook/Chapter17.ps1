## Chapter17 in book "Windows PowerShell Cookbook"
## Chapter17. Extend the Reach of Windows PowerShell


##17.1. Automate Programs Using COM Scripting Interfaces
$shell = New-Object -ComObject "Shell.Application"
$shell.Windows() | Format-Table LocationName,LocationUrl


##17.2. Program: Query a SQL Data Source
#Example 17-1. Invoke-SqlCommand.ps1


##17.3. Access Windows Performance Counters
#Example 17-2. Accessing performance counter data through the Get-Counter cmdlet
# To retrieve information about a specific performance counter, use the Get-Counter cmdlet
$counter = Get-Counter "\System\System Up Time"
$uptime = $counter.CounterSamples[0].CookedValue
New-TimeSpan -Seconds $uptime

# Alternatively, WMI¡¯s Win32_Perf* set of classes supports many of the most common performance counters
Get-CimInstance Win32_PerfFormattedData_Tcpip_NetworkInterface

#The Get-Counter cmdlet provides handy access to all Windows performance counters.  With no parameters, it summarizes system activity
Get-Counter -Continuous

$computer = $ENV:Computername
Get-Counter -Computer $computer "processor(_total)\% processor time"

Get-Counter -List * | Format-List CounterSetName,Description

Get-Counter -ListSet * | Where-Object { $_.Description -match "garbage" }
Get-Counter -ListSet * | Where-Object { $_.Paths -match "Gen 2 heap" }


##17.4. Access Windows API Functions
#Example 17-3. Get-PrivateProfileString.ps1


##17.5. Program: Invoke Simple Windows API Calls
#Example 17-4. Invoke-WindowsApi.ps1


##17.6. Define or Extend a .NET Class
#Example 17-5. Invoke-AddTypeTypeDefinition.ps1
$cmdlet = @'
using System.Management.Automation;
namespace PowerShellCookbook
{
	[Cmdlet("Invoke", "NewCmdlet")]
	public class InvokeNewCmdletCommand : Cmdlet
	{
		[Parameter(Mandatory = true)]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}
		private string _name;
		protected override void BeginProcessing()
		{
			WriteObject("Hello " + _name);
		}
	}
}
'@
Add-Type -TypeDefinition $cmdlet -OutputAssembly MyNewModule.dll
Import-Module .\MyNewModule.dll
Invoke-NewCmdlet


##17.7. Add Inline C# to Your PowerShell Script
#Example 17-7. Interacting with classes from the SharpZipLib SDK DLL
Add-Type -Path d:\bin\ICSharpCode.SharpZipLib.dll
$namespace = "ICSharpCode.SharpZipLib.Zip.{0}"
$zipName = Join-Path (Get-Location) "PowerShell_Scripts.zip"
$zipFile = New-Object ($namespace -f "ZipOutputStream") ([IO.File]::Create($zipName))
foreach($file in dir *.ps1)
{
	## Add the file to the ZIP archive.
	$zipEntry = New-Object ($namespace -f "ZipEntry") $file.Name
	$zipFile.PutNextEntry($zipEntry)
}
$zipFile.Close()

./Invoke-Inline.ps1


##17.8. Access a .NET SDK Library


##17.9. Create Your Own PowerShell Cmdlet


##17.10. Add PowerShell Scripting to Your Own Program
