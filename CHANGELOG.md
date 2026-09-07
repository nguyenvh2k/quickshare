# Changelog

## 20260830.01

- Rewrite the Name Card exchange: set up a personal contact card in Settings and exchange it Bada-to-Bada with an NFC tap. (#251)
- Present the incoming-transfer consent as a bottom sheet that opens by itself the moment a transfer arrives, floating over whatever is on screen in its own task; the notification becomes a plain banner (no clipped buttons on OEM shades) whose tap opens the same sheet, and the completed-state photo blur now fills the sheet shape. (#279)
- Add an instant pressed wash to every frosted button so taps read immediately, including the send sheet's Cancel/Done plate where the ripple was invisible over the backdrop blur. (#278)
- Fix the send picker's peer-device names to inherit the DayNight theme text color instead of remaining dark in night mode. (#275)
- Harden the Bluetooth-to-Wi-Fi-Direct bandwidth upgrade: solicit only at upgrade-loop entry while still on Bluetooth, scale the pre-payload offer wait by time since the entry request, and track prior-channel teardown commitment on both roles. (#265, #266, #268)
- Stop MdnsAdvertisementGate from flooding the diagnostics log during peer sessions. (#269)
