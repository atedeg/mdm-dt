package dev.atedeg.mdm

enum IncomingEvents:
  case TemperatureOutOfRange(temperature: Temperature, device: String)
  case PhOutOfRange(ph: Ph, device: String)
