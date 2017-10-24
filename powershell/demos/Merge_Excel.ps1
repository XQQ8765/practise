<#
1. Generate 3 excel files to save the top 5 processes: top0_5.xlsx, top6_10.xlsx, top11_15.xlsx
2. Merge the rows in the above excel files into 1 single Excel file: "merged_excel.xlsx"
Reference to: http://www.pstips.net/powershell-%E5%90%88%E5%B9%B6excel.html PowerShell ºÏ²¢excel
#>

$excel_folder = "d:/tmp"
$excel_filenames = "top0_5.xlsx", "top6_10.xlsx", "top11_15.xlsx"
$merged_filename = "merged_excel.xlsx"
$script:merged_row_index =0

function Generate-Excels()
{
	$top15_processes = ps | sort -Descending WS | select -first 15 | select Name, Id, CPU, WorkingSet
    $processobjects = $top15_processes | % {
        [pscustomobject]@{name=$_.name; id=$_.id; cpu=[int] $_.CPU; workingset="{0:N0}" -f ($_.WorkingSet/1MB)}
    }

    write-host "---------write-host processobjects:" 
    $processobjects | Out-String | write-host
	$i = 0
	Generate-Excel -filename $excel_filenames[0] -processes $processobjects[0..4]
    Generate-Excel -filename $excel_filenames[1] -processes $processobjects[5..9]
    Generate-Excel -filename $excel_filenames[2] -processes $processobjects[10..14]
}

function Generate-Excel([string] $filename, [object[]] $processes) {
	$objExcel = New-Object -comobject Excel.Application
	$objExcel.Visible = $False
	$objWorkbook = $objExcel.Workbooks.Add()
	$objWorksheet = $objWorkbook.Worksheets.Item(1)

	$i = 1
	$objWorksheet.Cells.Item($i, 1) = "Name"
	$objWorksheet.Cells.Item($i, 2) = "Id"
	$objWorksheet.Cells.Item($i, 3) = "CPU (s)"
	$objWorksheet.Cells.Item($i, 4) = "WorkingSet (M)"
	$processes | % {
		$i++; 
		$objWorksheet.Cells.Item($i,1) = $_.name 
		$objWorksheet.Cells.Item($i,2) = $_.id
		$objWorksheet.Cells.Item($i,3) = $_.cpu
		$objWorksheet.Cells.Item($i,4) = $_.workingset
	}

	$savedfilepath = Join-Path $excel_folder $filename
	$objWorksheet.SaveAs($savedfilepath)
    write-host "---------created excel file:" $savedfilepath

	$objExcel.Workbooks.close()
    $objExcel.Quit()
}

function Merge-Excels() {
    $excel_files = $excel_filenames | % {Join-Path $excel_folder $_}
    write-host "---------excel_files:" 
    $excel_files | write-host

    $objExcel = New-Object -comobject Excel.Application
	$objExcel.Visible = $False
	$objWorkbook = $objExcel.Workbooks.Add()
	$objWorksheet = $objWorkbook.Worksheets.Item(1)

    $i = 1
	$objWorksheet.Cells.Item($i, 1) = "Name"
	$objWorksheet.Cells.Item($i, 2) = "Id"
	$objWorksheet.Cells.Item($i, 3) = "CPU (s)"
	$objWorksheet.Cells.Item($i, 4) = "WorkingSet (M)"

    #Merge the excel_files into $objExcel
    $script:merged_row_index = ++$i
    $excel_files | %{Merge-Excel -excel_filename $_ -worksheet $objWorksheet}

    $savedfilepath = Join-Path $excel_folder $merged_filename
	$objWorksheet.SaveAs($savedfilepath)
    
	$objExcel.Workbooks.close()
    $objExcel.Quit()
    write-host "---------The merged file is saved into: " $savedfilepath
}

function Merge-Excel([string] $excel_filename, [object] $worksheet) {
    write-host "---------Start to merge excel file: " $excel_filename
    $itemExcel = New-Object -ComObject Excel.Application
    $itemExcel.Visible = $False
    $itemWorkBook = $itemExcel.Workbooks.Open($excel_filename)
    $itemWorksheet = $itemWorkBook.Worksheets.Item(1)

    for($i = 2; $i -lt 7; $i++) {
        for ($j = 1; $j -lt 5; $j++) {
            $worksheet.Cells.Item($script:merged_row_index, $j) = $itemWorksheet.Cells.Item($i, $j)
        }
        $script:merged_row_index++
    }

    $itemExcel.Workbooks.close()
    $itemExcel.Quit()
    write-host "---------Complete to merge excel file: " $excel_filename
}

. Generate-Excels
. Merge-Excels
