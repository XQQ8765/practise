$job = Start-Job -Name TenSecondSleep { Start-Sleep 10 }
Register-TemporaryEvent $job StateChanged -Action {
    [Console]::Beep(100,100)
    Write-Host "Job #$($sender.Id) ($($sender.Name)) complete."
}
Job #6 (TenSecondSleep) complete.