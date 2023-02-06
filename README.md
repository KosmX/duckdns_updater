# DuckDNS update Kotlin version

A multiplatform CLI ddns updater, supporting IPv4 and IPv6 (or both at once)
> because I didn't find any supporting IPv6

## Build
`./gradle build` and get the file from `./build/libs`

simply create a `ddns.json`
```json
{
  "domain1": {
    "protocol": "ipv6",
    "token": "your-secret-token"
  },
  "domain2": {
    "protocol": "*",
    "token": "your-secret-token2",
    "failHard": false
  }
}
```

Protocol can be `ipv4`, `ipv6` or `*` for both  
`failHard` means exit the program on first error

You can add it to systemd on linux or Task Scheduler on Windows.
