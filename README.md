> [!NOTE]  
> On 29 June 2023, Mobius Software LTD signed an agreement with Mavenir to assume development and support of the RestcommOne on prem products.  For more details regarding open source / commercial differentiation, roadmap, licensing and other details please visit the [Mobius Website](https://www.mobius-software.com/telestaxannouncement).


# jdiameter
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bhttps%3A%2F%2Fgithub.com%2FRestComm%2Fjdiameter.svg?type=shield)](https://app.fossa.io/projects/git%2Bhttps%3A%2F%2Fgithub.com%2FRestComm%2Fjdiameter?ref=badge_shield)

RestComm jDiameter Stack and Services

## Introduction

RestComm jDiameter provides an Open Source Java implementation of the Diameter standard for Authentication, Authorization, and Accounting (AAA). Implementing the Base Protocol as well as some of the most important and widely used applications, RestComm Diameter allows a fast development of IMS components, such as Application Server (AS), Home Subscriber Server (HSS), Call Session Control Function (CSCF), Subscriber Location Function (SLF), etc. Featuring an extensible architecture to provide support for new applications, as well as to adapt the core functionalities of the stack to a fully customized solution.

## Supported Applications

The supported applications includes Base, Credit-Control Application, Ro (Online Charging), Rf (Offline Charging), Sh, Gx, Cx/Dx, Gq', S6a and many more. It also features an extensible architecture that allows additional Diameter application modules to be plugged in.

## Advanced Features

RestComm Diameter features several advanced features such as High-Availability and Fault-Tolerance support at stack level (and at RestComm JAIN SLEE Resource Adaptors), statistics gathering for monitoring the stack health, overload monitor to avoid congestion, several management and monitoring options, and many more to assist the development experience.

## Integration

RestComm Diameter also includes integration interfaces for [SIP Servlets](https://github.com/RestComm/sip-servlets) and [JAIN SLEE Resource Adaptors](https://github.com/RestComm/jain-slee.diameter).
