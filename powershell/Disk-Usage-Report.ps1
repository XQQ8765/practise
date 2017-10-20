#The script refere to:
# * https://stackoverflow.com/questions/27798567/powershell-excel-save-and-close-after-run
# * https://stackoverflow.com/questions/27663165/powershell-disk-usage-report
$erroractionpreference = "SilentlyContinue"
$warningPreference = "Continue"
$a = New-Object -comobject Excel.Application
$a.visible = $True 
Add-Type -AssemblyName Microsoft.Office.Interop.Excel
$xlFixedFormat = [Microsoft.Office.Interop.Excel.XlFileFormat]::xlWorkbookDefault


$a.Visible = $true

$b = $a.Workbooks.Add()
$c = $b.Worksheets.Item(1)

$c.Cells.Item(1,1) = "Server Name"
$c.Cells.Item(1,2) = "Drive"
$c.Cells.Item(1,3) = "Total Size (GB)"
$c.Cells.Item(1,4) = "Free Space (GB)"
$c.Cells.Item(1,5) = "Free Space (%)"
$c.Cells.Item(1,6) = "Date"

$d = $c.UsedRange
$d.Interior.ColorIndex = 19
$d.Font.ColorIndex = 11
$d.Font.Bold = $True

$intRow = 2

#$colComputers = get-content "c:\servers.txt"
$colComputers = "localhost", "11.154.10.103", "dsg6dj7q52", "10.154.10.103"

foreach ($strComputer in $colComputers)
{
    $colDisks = get-wmiobject Win32_LogicalDisk -computername $strComputer -Filter "DriveType = 3"
    if ($colDisks -eq $null) {
        Write-Warning "Can not collect disk info form computer ${strComputer}."
        continue
    }
    foreach ($objdisk in $colDisks)
    {
        $c.Cells.Item($intRow, 1) = $strComputer.ToUpper()
        $c.Cells.Item($intRow, 2) = $objDisk.DeviceID
        $c.Cells.Item($intRow, 3) = "{0:N0}" -f ($objDisk.Size/1GB)
        $c.Cells.Item($intRow, 4) = "{0:N0}" -f ($objDisk.FreeSpace/1GB)
        $c.Cells.Item($intRow, 5) = "{0:P0}" -f ([double]$objDisk.FreeSpace/[double]$objDisk.Size)
        $c.Cells.Item($intRow, 6) = Get-Date
        $intRow = $intRow + 1
    }
    $colDisks = $null
}

#$a.workbooks.OpenText($file,437,1,1,1,$True,$True,$False,$False,$True,$False)
#$a.ActiveWorkbook.SaveAs("C:\Users\Username\Desktop\myfile.xls", $xlFixedFormat)

$savedfilepath = Join-Path ([Environment]::GetFolderPath("Desktop")) "disk_usage.xlsx"
$b.SaveAs($savedfilepath)

#TODO: research how to kill the excel process while closing the excel. 
#See: https://stackoverflow.com/questions/19908302/how-to-open-saveas-then-close-an-excel-2013-macro-enabled-workbook-from-powe
#View the excel processes: Get-Process -name "*excel*"
$a.Workbooks.Close()
$a.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($a)
Remove-Variable a