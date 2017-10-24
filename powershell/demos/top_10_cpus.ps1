$topProcesses = ps | sort -Property CPU -Descending | select -First 10 | select @{Name="cpu"; Expression = {[int] $_.CPU}}, Id, ProcessName, SI
if ($topProcesses.count -lt 1) {
    return
}

#Get the column names
$columns = New-Object Collections.Generic.List[String]
$topProcesses[0] | gm -MemberType NoteProperty | Select Name | % {$columns.Add($_.Name)}

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $true
$workbook = $excel.Workbooks.Add()
$sheet = $workbook.ActiveSheet

#Header
$columnIdex = 1
$columns | % {
    $sheet.cells.Item(1, $columnIdex++) = $_
}

$counter = 1
$topProcesses | % {
	$counter++
    $columnIdex = 1
    $process = $_
    $columns | % {
        $sheet.cells.Item($counter, $columnIdex++) = $process.($_)
    }
}

