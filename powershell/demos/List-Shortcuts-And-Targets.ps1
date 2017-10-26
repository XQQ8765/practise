﻿<#
See: 
http://www.computerperformance.co.uk/powershell/powershell_function_shortcut.htm
http://powershellblogger.com/2016/01/create-shortcuts-lnk-or-url-files-with-powershell
http://windowsitpro.com/powershell/working-shortcuts-windows-powershell
#>
# PowerShell function to list Start Menu Shortcuts
Function Get-StartMenu{
    Begin{
        Clear-Host
        $Path = "$Env:ProgramData\Microsoft\Windows\Start Menu\Programs"
        $x=0
    } # End of Begin
    Process {
        $StartMenu = Get-ChildItem $Path -Recurse -Include *.lnk
        ForEach($ShortCut in $StartMenu) {
            $Shell = New-Object -ComObject WScript.Shell
            $Properties = @{
                ShortcutName = $Shortcut.Name
                LinkTarget = $Shell.CreateShortcut($Shortcut).targetpath
            }
            New-Object PSObject -Property $Properties
            $x ++
        } #End of ForEach
        [Runtime.InteropServices.Marshal]::ReleaseComObject($Shell) | Out-Null
     } # End of Process
    End{
        "`nStart Menu items = $x "
    }
}
#Example of function in action:

Get-StartMenu | Sort ShortcutName | Ft ShortcutName, LinkTarget -Auto
