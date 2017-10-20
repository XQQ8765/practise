$topProcesses = ps | sort -Property CPU -Descending | select -First 10 | select @{Name="cpu"; Expression = {[int] $_.CPU}}, Id, ProcessName
if ($topProcesses.count -lt 1) {
    return
}


$excel = New-Object -ComObject Excel.Application
$excel.Visible = $true
$workbook = $excel.Workbooks.Add()
$sheet = $workbook.ActiveSheet

$sheet.cells.Item(1,1) = "CPU"
$sheet.cells.Item(1,2) = "Id"
$sheet.cells.Item(1,3) = "ProcessName"

$topProcesses

$counter = 1
$topProcesses | % {
	$counter++
	$sheet.cells.Item($counter,1) = $_.cpu
    $sheet.cells.Item($counter,2) = $_.Id
    $sheet.cells.Item($counter,3) = $_.ProcessName
}

