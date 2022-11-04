+++
title = "MDM DT"
outputs = ["Reveal"]
+++

# Pervasive Computing

## MDM

Nicolas Farabegoli & Linda Vitali

---

# About this project

We decided to model the domain og the _Mambelli_ cheese factory:  
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

{{< container >}}

{{< column >}}

<ul>
<li>Different manufacturers creates their own <b>protocols</b> and <b>formats</b></li>
<li>Interoperability and integration occurs via an <b>agreements</b> between the different parties</li>
<li>The <b>interoperability</b> problem is not perceived as a problem by the entrepreneur</li>
</ul>

<p>
The <b>Industry 4.0</b> try to arginate this problem forcing the companies to reach a certain degree of integration and interoperability.
</p>

{{< /column >}}
{{< column >}}

{{< figure src="img/interoperability.png" caption="Interoperability attention in industrial sectors. [[1]](https://www.sciencedirect.com/science/article/pii/S2405896317317615)" >}}

{{< /column >}}

{{< /container >}}

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
