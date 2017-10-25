<#
Merge the excel sheets into 1 excel file
Reference to: 
http://www.pstips.net/powershell-%E5%90%88%E5%B9%B6excel.html PowerShell 合并excel
https://stackoverflow.com/questions/25880652/powershell-excel-rename-worksheets-base-upon-filename
https://social.technet.microsoft.com/Forums/scriptcenter/en-US/2a0dc55d-66c4-4228-a834-0389a32e52b0/how-can-i-use-powershell-to-copy-an-excel-worksheet-from-one-workbook-to-the-second-sheet-of-another?forum=ITCG
http://www.lazywinadmin.com/2014/03/powershell-read-excel-file-using-com.html
https://stackoverflow.com/questions/39044995/how-can-i-stop-excel-processes-from-running-in-the-background-after-a-powershell
#>
$excel_folder = "d:/tmp"
$merged_filename = "merged_excel_tabs.xlsx"

$excel_files = dir $excel_folder -Name "*.xlsx" | sls -n $merged_filename
if ($excel_files.Count -lt 1) {
    return
}

write-host "---------excel_files:" $excel_files

$objExcel = New-Object -comobject Excel.Application
$objExcel.Visible = $False
$objExcel.displayAlerts = $false # don't prompt the user
$objWorkbook = $objExcel.Workbooks.Add()
$originSheets = “Sheet1”, "Sheet2", "Sheet3" | %{ $objWorkbook.Sheets.Item($_) }

$excel_files | %{
    Write-Host "Start to copy worksheet in file " $_
    $item_path = Join-Path $excel_folder $_
    $itemWorkBook = $objExcel.Workbooks.Open($item_path, $null, $true)#Open with readonly
    $itemWorksheet = $itemWorkBook.Worksheets.Item(1)

    $destWorksheetName = "{0}_{1}" -f (first8chars -str $_), (first8chars -str $itemWorksheet.Name)
    #Renaming the sheet prior to copying, See https://stackoverflow.com/questions/25880652/powershell-excel-rename-worksheets-base-upon-filename
    $itemWorksheet.name = $destWorksheetName

    $destWorksheet = $objWorkbook.Worksheets.Item(1)
    $itemWorksheet.Copy($destWorksheet)
    
    $itemWorkBook.close($false)
    Write-Host "Complete to copy worksheet " $destWorksheetName
}


Write-Host "Complete to merge sheets into excel file " $savedfilepath

#Delete the origin sheets
$originSheets.delete()

$savedfilepath = Join-Path $excel_folder $merged_filename
$objWorkbook.SaveAs($savedfilepath)
$objExcel.Workbooks.close()
$objExcel.Quit()

#TODO: The background excel process still is running evan after the script complete, find a way to kill the excel process 
#https://stackoverflow.com/questions/39044995/how-can-i-stop-excel-processes-from-running-in-the-background-after-a-powershell


function first8chars([string] $str) {
    if ($str -eq $null) {
        return ""
    }
    $str = $str.split('.')[0]
    if ($str.length -gt 8) {
        return $str.Substring(0,8)
    }
    return $str
}