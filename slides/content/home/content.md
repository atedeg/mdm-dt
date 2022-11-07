+++
title = "MDM DT"
outputs = ["Reveal"]
+++

# About this project

We decided to model the domain of the _Mambelli_ cheese factory:  
a small family business that produces and sells cheese.

We choose this domain because:

- It is quite complex
- There are domain expert that can help us
- We ❤️ cheese

---

# Introduction

The project models the business processes of the _Mambelli_ cheese factory so that they can benefit from technological and digital support going in a direction of **Integration**, **resource-saving** and **innovation**.

## Project challenges

- conduct a Digital Twin modelling following **DDD** methodologies
- **data integration** from different sources and machines manufacturers
- **predict** breakdowns and maintenance needs

---

# Interoperability

One of the main problems in the industry is the lack of interoperability between different machines and systems.

{{% container %}}

{{% column %}}

- Different manufacturers creates their own **protocols** and **formats**
- Interoperability and integration occurs via an **agreements** between the different parties
- The **interoperability** problem is not perceived as a problem by the entrepreneur

The **Industry 4.0** try to arginate this problem forcing the companies to reach a certain degree of integration and interoperability.

{{% /column %}}
{{% column %}}

{{< figure src="img/interoperability.png" caption="Interoperability attention in industrial sectors. [[1]](https://www.sciencedirect.com/science/article/pii/S2405896317317615)" >}}

{{% /column %}}

{{% /container %}}

---
{{% section %}}

# Domain Anaylsis

---

## Event Storming

![event storming animation](https://atedeg.dev/mdm-slides/img/event-storming-session.gif)

---

## Event Storming

![event storming](https://atedeg.dev/mdm-slides/img/event-storming.svg)

---

## Digital-Twin-related subdomains

{{< figure src="img/event-storming.png" >}}

---

## Core domain chart

{{< figure src="img/EventStorming-core-domain-chart.svg" width="120%" >}}

{{% /section %}}

---
{{% section %}}

# Digital twin Motivations

---

# Milk Tank

Milk storage is crucial for several reasons: Milk **yield**, **quality** of the final product and parameters to be met

At present:

- Milk storage is done **manually**
- Milk analysis are conducted before each production
- No **real-time** monitoring of the milk
- No integration with the company's IT infrastructure

---

# Milk Tank - Digital Twin

To solve the weakness discussed before, a digital twin can be used to:

- Guarantee **real-time** monitoring of the milk's parameters
- Give accurate information about the milk's **quality** used by other subdomains (production and stocking)
- Raise **events** if the milk's parameters are not in the range of the allowed values
- Integrate with the company's IT infrastructure

---

# Packaging Machine

The packaging machine is a very important machine in the packaging process but it is fairly prone to breakdowns.

At present:

- The machine is **set manually** by the operator
- No preventive alters showing a potential failure causing a delay in the packaging line
- Extraordinary maintenance cause **dead time** in the production line

---

# Packaging Machine - Digital Twin

To solve the weakness discussed before, a digital twin can be used to:

- Automatically **set** the machine based on the batch to be packaged
- **Predict** the failure of the machine
- Generate **statistics** about the machine's performance

---

# Metal Detector

A metal detector is an indispensable tool in the quality assurance process, stringent regulations
require its use within the packaging line.

At present:

- Has no information about the **current batch**
- Data produced is not integrated with the company's information system

With a digital twin we can better integrate the data that the metal detector produce and make it able to easily communicate with the other machines.

---

# Scale

Just as with the metal detector, the scale is also subject to _stringent regulations_.
The measurement of product weight must meet legal parameters outside of which the product cannot be sold.

At present:

- The scale is **set manually** by the operator
- Simple reports are generated but rarely used

A digital twin can help integrate such data into the system as well as monitor the weight of the package and _detect anomalies_ in the packaging process.

{{% /section %}}

---

# Supporting Bounded Contexts

Having introduced _digital twins_ we noticed that other subdomains or bounded contexts were
needed in order to support their functioning. In particular, we identified the following bounded
contexts:

- **Alerts**: manages the alerts sent by the digital twins
- **Maintenance**: manages the maintenance of the Packaging Machine digital twin
- **Reporting**: manages the reports sent by the Scale and the Metal Detector digital twins

---

# Digital Twins Interactions

{{< figure src="img/EventStorming-DT.svg" >}}

---

# Architecture

{{< figure src="img/clean-architecture.png" >}}

---

# Context Map

{{< figure src="img/context-map.png" >}}

---

{{% section %}}

# Digital Twin Models

All the models described in the following slides are built using the _Web Thing Model_ specification [[2]](https://www.w3.org/Submission/wot-model/).

This specification enable the description of a virtual counterpart of a _physical object_ in the **Web of Things**.

---

# Milk Tank Model

{{% container %}}
{{% column %}}
Web Thing Model _milk tank_

```json
{
  "@context": ...
  "@type": "tm:ThingModel",
  "title": "Milk Tank",
  "links": [
    {
      "rel": "tm:submodel",
      "href": ...,
      "instanceName": "pH"
    },
    {
      "rel": "tm:submodel",
      "href": ...,
      "instanceName": "Temperature"
    }
  ],
  "properties": {
    "manufacturer": {...},
    "serialNo": {...},
    "availableMilk": {...}
  },
  "actions": {
    "open-valve": {...}
  }
}
```

{{% /column %}}

{{% column %}}

- **`manufacturer`** and **`serialNo`** are static properties belonging to the specific machine
- **`availableMilk`** show in real-time the quantity of milk stocked in the tank
- The **`open-valve`** action is use to open or close the valve to start the production
- The model is _linked_ of other two models: `pH` and `Temperature`

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/milk-tank/milk-tank.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

## PH model

{{% container %}}
{{% column %}}
Web Thing Model _pH_

```json
{
  "@context": ...
  "@type": "tm:ThingModel",
  "properties": {
    "pH": { "type": "number" },
    "min-pH": {
        "type": "number",
        "minimum": 0.0 
    },
    "max-pH": {
        "type": "number",
        "minimum": 0.0 
    }
  },
  "events": {
    "ph-out-of-range": {
      "data": {
        "@type": "om2:Quantity",
        "type": "number",
        "unit": "om2:Quantity",
        "minimum": 0.0
      }
    }
  }
}
```

{{% /column %}}

{{% column %}}

- The **`Ph`** property shows in real-time the current pH value
- The **`min-pH`** property that set the minimum accepted pH value
- The **`max-pH`** property that set the maximum accepted pH value
- The **`ph-out-of-range`** this event is triggered when the pH is out of range

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/milk-tank/ph.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

## Temperature model

{{% container %}}
{{% column %}}
Web Thing Model _Temperature_

```json
{
    "@context": ...
    "@type": "tm:ThingModel",
    "properties": {
        "temperature": { "type": "number" },
        "min-temp": { "type": "number" },
        "max-temp": { "type": "number" }
    },
    "events": {
        "temperature-out-of-range": {
            "title": "Alarm for temperature out of range",
            "data": {
                "@type": "om2:Temperature",
                "title": "Temperature value",
                "type": "number",
                "unit": "om2:CelsiusTemperature",
            }   
        }
    }
}
```

{{% /column %}}

{{% column %}}

- The **`temperature`** property shows in real-time the current temperature value
- The **`min-temp`** property that set the minimum accepted temperature value
- The **`max-temp`** property that set the maximum accepted temperature value
- The **`temperature-out-of-range`** this event is triggered when the temperature is out of range

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/milk-tank/ph.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

# Milk Tank

{{% container %}}
{{% column %}}

As shown in the definition of the _Milk Tank_, it is "linked" with a pH and temperature sensor.

{{< mermaid >}}
graph LR
  dt([Milk Tank\nDigital Twin]) -.-> ph([pH Sensor])
  dt -.-> temp([Temperature Sensor])

{{< /mermaid >}}

{{% /column %}}
{{% column %}}

The _Milk Tank_ has interactions with the **Alarm** and **Milk Planning** bounded context

{{< mermaid >}}
graph TD
  dt([Milk Tank\nDigital Twin]) == "ph-out-of-range" ==> alarm_bc[Alarm\nBounded Context]
  dt == "temperature-out-of-range" ==> alarm_bc
  dt -. availableMilk .- milk_planning_bc[Milk Planning\nBounded Context]
  prod[Production\nBounded Context] -.-> dt
{{< /mermaid >}}

{{% /column %}}
{{% /container %}}

---

# Packaging Machine Model

{{% container %}}
{{% column %}}
Web Thing Model _Packaging Machine_

```json
{
    "@context": ...,
    "@type": "tm:ThingModel",
    "properties": {
      "manufacturer": {...},
      "serialNo": {...},
      "currentBatch": {...}
    },
    "actions": {
        "start-packaging": {
            "type": "boolean"
        }
    },
    "events": {
      "packaging-started": {
        "data": {
          "@type": "Boolean",
          "type": "boolean",
        },
      },
      "packagingMachine-failure": {
        "type": "object",
        "properties": {
            "batch-id": { "type": "string" },
            "cutter-temperature": { "type": "integer" }
        }
      },
      "package-damaged": {
        "type": "object",
        "properties": {
            "batch-id": { "type": "string" },
            "cutter-temperature": { "type": "integer" }
        }
      }
    }
  }
```

{{% /column %}}

{{% column %}}

- **`currentBatch`** property is set wen a new batch should be packaged
- **`start-packaging`** is the available action that trigger the start of the packaging process
- The **`packaging-started`** event is triggered on the start of the packaging process
- The **`packagingMachine-failure`** event is triggered when a failure in the machine occurs
- The **`package-damaged`** event is triggered when the machine damage a package during the packaging

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/packaging-machine/packaging-machine.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

# Packaging Machine

{{% container %}}
{{% column %}}
The _Packaging Machine_ is "linked" with the temperature sensor.

{{< mermaid >}}
graph LR
  dt([Packaging Machine\nDigital Twin]) -...-> temp([Temperature Sensor])

{{< /mermaid >}}
{{% /column %}}
{{% column %}}

The _Packaging Machine_ has interactions with the **Alarm**, **Report** and **Maintenance** bounded context

{{< mermaid >}}
graph TD
  dt([Packaging Machine\nDigital Twin]) == "packagingMachine-failure" ==> alarm_bc[Alarm\nBounded Context]
  dt == "package-damaged" ==> alarm_bc
  dt == "packaging-started" ==> stocking_bc([Stocking\nBounded Context])
  dt == "temperature-out-of-range" ==> alarm_bc
{{< /mermaid >}}
{{% /column %}}
{{% /container %}}

---

# Metal Detector Model

{{% container %}}
{{% column %}}
Web Thing Model _Metal Detector_

```json
{
  "@context": [...],
  "@type": "tm:ThingModel",
  "properties": {
    "manufacturer": {...},
    "serialNo": {...},
    "currentBatch": {
      "type": "string"
    }
  },
  "events": {
    "dropped-packages": {
      "type": "object",
      "properties": {
          "batch-id": { "type": "string" },
          "dropped-packages": { "type": "integer" }
      }
    }
  }
}
```

{{% /column %}}

{{% column %}}

- **`currentBatch`** property is set wen a new batch should be packaged
- The **`dropped-packages`** event is triggered when a package contains a metal debris

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/metal-detector/metal-detector.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

# Metal Detector

The _Metal Detector_ interacts with the **Stocking**, **Alert** and **Reporting** bounded context.

{{< mermaid >}}
flowchart TD
  dt([Metal Detector\nDigital Twin]) == "dropped-packages" ==> stocking[Stocking\nBounded Context]
  dt == "dropped-packages" ==> report[Reporting\nBounded Context]
  dt == "dropped-packages" ==> alert[Alert\nBounded Context]
{{< /mermaid >}}

---

# Scale Model

{{% container %}}
{{% column %}}
Web Thing Model _Scale_

```json
{
  "@context": [...],
  "@type": "tm:ThingModel",
  "properties": {
      "manufacturer": {...},
      "serialNo": {...},
      "currentBatch": {...},
      "meanWeight": {...},
      "stdDevWeight": {...}
  },
  "events": {
    "dropped-packages": {
      "type": "object",
      "properties": {
        "batch-id": { "type": "string" },
        "dropped-packages": { "type": "integer" }
      }
    },
    "batch-completed": {
      "type": "object",
      "properties": {
        "completed": { "type": "boolean" },
        "batch-id": { "type": "string" },
        "dropped-packages": { "type": "integer" }
      }
    }
  }
}
```

{{% /column %}}

{{% column %}}

- The **`currentBatch`** property is set wen a new batch should be packaged
- The **`meanWeight`** property shows the mean weight for the current batch
- The **`stdDevWeight`** property shows the standard deviation of the weight for the current batch
- The **`dropped-packages`** event is triggered at the end of the QA process if there are some dropped packages
- The **`batch-completed`** event is triggered at the end of the QA process

The complete _Web Thing Model_ is available [here](https://github.com/atedeg/mdm-dt/blob/main/things-models/metal-detector/metal-detector.tm.jsonld).

{{% /column %}}
{{% /container %}}

---

# Scale

The _Scale Digital Twin_ has interactions with the **Stocking**, **Alert** and **Reporting** bounded context.

{{< mermaid >}}
%%{init: {"theme": "default", 'themeVariables': { 'fontFamily': 'Inter' }}}%%
flowchart TD
  dt([Scale\nDigital Twin]) == batch-completed ==> stocking[Stocking\nBounded Context]
  dt == dropped-packages ==> alert[Alert\nBounded Context]
  dt == dropped-packages ==> reporting[Reporting\nBounded Context]
{{< /mermaid >}}

{{% /section %}}

---


{{% section %}}

# Development

---

# Eclipse Ditto

**Ditto** is an open-source technology for building simple digital twins that are mirroring some
devices.

{{% container %}}
{{% column %}}

- _Policies:_ manages persistence and enforcement of Policies
- _Things:_ manages persistence and enforcement of Things and Features
- _Things-Search:_ that tracks changes to Things, Features, Policies and updates an optimized
search index
- _Gateway:_ that provides HTTP and WebSocket API
- _Connectivity:_ manages the persistence of Connections as well as sends Ditto Protocol
messages to external message brokers and receives messages from them

{{% /column %}}

{{% column %}}
{{< figure src="img/ditto-architecture.png" width="70%" >}}
{{% /column %}}

{{% /container %}}

---

# Web Of Things integration

From the version `2.4.0` of Ditto they support the **W3C WoT** specification, so you can declare a digital twin using a **Thing Model**.

This integration takes a big step forward towards:

- increased **interoperability**
- _introspection_ of twins to find out their capabilities
- addition of **semantic** context to Ditto managed digital twins and their capabilities
- description of Ditto twin HTTP APIs in an open, established, well specified, "web optimized", active IoT standard
- opening the door to a **new ecosystem** of tools

---

# Ditto vs WoT

{{% container %}}
{{% column %}}
#### _Thing level_

| **WoT** | **Ditto** |
|-----------------|-------------------|
| _Thing_ | Ditto Thing |
| _Properties_ | Thing attributes |
| _Actions_ | Thing messages with Direction to (“inbox”) of a Thing ID.|
| _Events_ | Thing messages with Direction from (“outbox”) of a Thing ID. |
| Composition via _tm:submodule_ | Thing features representing different aspects of a Ditto Thing. |
{{% /column %}}

{{% column %}}
#### _Feature level_

| **WoT** | **Ditto** |
|-----------------|-------------------|
| _Thing_ | Feature. In Ditto, a Feature is an aspect of a Ditto Thing. |
| _Properties_ | Feature properties |
| _Actions_ | Feature messages with Direction to (“inbox”) of a Thing ID + Feature ID combination. |
| _Events_ | Feature messages with Direction from (“outbox”) of a Thing ID + Feature ID combination. |
{{% /column %}}
{{% /container %}}

---

# Implementation

The code base of the project is written in _Scala 3_ using a **monadic** approach.

Handler defined in the **Alerts** bounded context:

```scala
def handlePackagingMachineFailureEvent[M[_]: Monad: LiftIO: CanRaise[String]](
    event: PackagingMachineFailedDTO,
): M[Unit] =
    for
        r <- validate(event)
        message = managePackagingMachineFailure(PackagingMachineFailure(LocalTime.now(), r.batchID, r.cutterTemperature))
        _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
        _ <- sendEmail(message)
    yield ()
```

Handler defined in the **Maintenance** bounded context:

```scala
def packagingMachineFailureHandler[M[_]: Monad: LiftIO: CanRaise[String]](
    failure: PackagingMachineFailureDTO,
): M[Unit] =
    for
        failureEvent <- validate(failure)
        _ <- collectMachineFailure(failureEvent)
        _ <- writeFailureToDB(failureEvent)
    yield ()
```

---

# DTO

All events are represented with external entities that need to be transformed into **domain entities**.

This was done by implementing a set of _DTOs_ (Data Transfer Objects) for each event, which is
used to _validate_ the incoming events and transform them into _domain entities._

A code example of the Packaging Machine Failure DTO:

```scala
final case class PackagingMachineMaintenanceDTO(maintenance: String)
object PackagingMachineMaintenanceDTO:
  given DTO[PackagingMachineFailure, PackagingMachineFailureDTO] = productTypeDTO
```

{{% /section %}}

---

# Conclusions

- Use of **DDD** methodologies for **Digital Twin** modelling
- Some limitation are emerged using this approach
- Manage the communication with the digital twin through **DTOs**
- Explore the opportunity of **Ditto** + **WoT** (_Thing Model_ )
- Positive feedback from _Domain Experts_

---

# Future Works

- Some of the code is mocked
- Exploit simulation in order to make a more accurate prediction of the **RUL**
  - _Packaging machine_ predictive maintenance
