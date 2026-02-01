---
tags: [misc]
---

# RFC 6648 - 애플리케이션 프로토콜에서 "X-" 접두사 및 유사 구조의 사용 중단 (Deprecating the "X-" Prefix and Similar Constructs in Application Protocols)

**저자:** P. Saint-Andre (Cisco), D. Crocker (BBIW), M. Nottingham (Rackspace)  
**날짜:** 2012년 6월  
**카테고리:** 모범 사례 (Best Current Practice)  
**ISSN:** 2070-1721  
**BCP:** 178  
**RFC:** 6648  

## 요약 (Abstract)

역사적으로 애플리케이션 프로토콜의 설계자와 구현자들은 비표준 매개변수 이름 앞에 "X-" 문자열이나 유사한 구조를 붙여 표준 매개변수와 구별해 왔습니다. 그러나 실제로 이러한 관행은 문제 해결보다 더 많은 문제를 야기했습니다. 따라서 이 문서는 애플리케이션 프로토콜에서 텍스트(숫자가 아닌) 이름을 가진 새로 정의되는 매개변수에 대해 이 관행을 중단(deprecate)합니다.

## 이 메모의 상태 (Status of This Memo)

이 메모는 인터넷 모범 사례(Internet Best Current Practice)를 문서화합니다.

이 문서는 국제 인터넷 표준화 기구(IETF)의 산출물입니다. 이는 IETF 커뮤니티의 합의를 나타냅니다. 공개 검토를 거쳤으며 인터넷 엔지니어링 운영 그룹(IESG)의 승인을 받아 발행되었습니다. BCP에 대한 추가 정보는 [RFC 5741의 섹션 2](https://datatracker.ietf.org/doc/html/rfc5741#section-2)에서 확인할 수 있습니다.

이 문서의 현재 상태, 오류(errata), 피드백 제공 방법에 대한 정보는 [http://www.rfc-editor.org/info/rfc6648](http://www.rfc-editor.org/info/rfc6648)에서 얻을 수 있습니다.

## 저작권 고지 (Copyright Notice)

Copyright (c) 2012 IETF Trust and the persons identified as the document authors. All rights reserved.

이 문서는 [BCP 78](https://datatracker.ietf.org/doc/html/bcp78) 및 IETF 문서와 관련된 IETF Trust의 법적 조항([http://trustee.ietf.org/license-info](http://trustee.ietf.org/license-info))의 적용을 받습니다. 이 문서에 대한 귀하의 권리와 제한 사항을 설명하고 있으므로 주의 깊게 검토하시기 바랍니다. 이 문서에서 추출된 코드 구성 요소는 Trust 법적 조항의 섹션 4.e에 설명된 대로 Simplified BSD License 텍스트를 포함해야 하며, Simplified BSD License에 설명된 대로 보증 없이 제공됩니다.

---

## 목차 (Table of Contents)

1. [소개 (Introduction)](#1-소개-introduction)
2. [애플리케이션 프로토콜 구현자를 위한 권장 사항](#2-애플리케이션-프로토콜-구현자를-위한-권장-사항)
3. [새로운 매개변수 생성자를 위한 권장 사항](#3-새로운-매개변수-생성자를-위한-권장-사항)
4. [프로토콜 설계자를 위한 권장 사항](#4-프로토콜-설계자를-위한-권장-사항)
5. [보안 고려 사항 (Security Considerations)](#5-보안-고려-사항-security-considerations)
6. [IANA 고려 사항 (IANA Considerations)](#6-iana-고려-사항-iana-considerations)
7. [감사의 글 (Acknowledgements)](#7-감사의-글-acknowledgements)
- [부록 A. 배경 (Background)](#부록-a-배경-background)
- [부록 B. 분석 (Analysis)](#부록-b-분석-analysis)
- [참고 문헌 (References)](#참고-문헌-references)

---

## 1. 소개 (Introduction)

많은 애플리케이션 프로토콜은 데이터를 식별하기 위해 텍스트(숫자가 아닌) 이름을 가진 매개변수를 사용합니다(예: 미디어 타입, 인터넷 메일 메시지 및 HTTP 요청의 헤더 필드, vCard 매개변수 및 속성 등).

역사적으로 애플리케이션 프로토콜의 설계자와 구현자들은 비표준 매개변수 이름 앞에 "X-" 문자열이나 유사한 구조(예: "x.")를 붙여 표준 매개변수와 구별해 왔습니다. 여기서 "X"는 일반적으로 "eXperimental"(실험적) 또는 "eXtension"(확장)을 의미하는 것으로 이해되었습니다.

이 관행에 따르면 매개변수의 이름은 데이터를 식별할 뿐만 아니라 매개변수의 상태(status)를 이름 자체에 포함하고 있었습니다. 즉, 공인된 표준 개발 기구에서 생성한 사양에 정의된(또는 해당 사양에 정의된 프로세스에 따라 등록된) 매개변수는 "X-" 또는 유사한 구조로 시작하지 않았고, 그러한 사양이나 프로세스 외부에서 정의된 매개변수는 "X-" 또는 유사한 구조로 시작했습니다.

[부록 A](#부록-a-배경-background)에서 더 자세히 설명하겠지만, 이 관행은 파일 전송(FTP), 이메일, 월드 와이드 웹(WWW)과 같은 애플리케이션 프로토콜에서 수년 동안 장려되었습니다. 특히 이메일의 경우 [[RFC822]](https://datatracker.ietf.org/doc/html/rfc822)에서 "Extension-fields"(확장 필드)와 "user-defined-fields"(사용자 정의 필드)를 구별함으로써 명문화되었으나, 이후 구현 및 배포 경험을 바탕으로 [[RFC2822]](https://datatracker.ietf.org/doc/html/rfc2822)에서 제거되었습니다. SIP 기술에서도 "P-" 헤더와 관련하여 유사한 과정이 있었으며, 이는 [[RFC5727]](https://datatracker.ietf.org/doc/html/rfc5727)에 설명되어 있습니다. 이러한 변경의 배경이 되는 이유는 [부록 B](#부록-b-분석-analysis)에서 살펴봅니다.

요약하자면, 이론적으로 "X-" 관행은 표준 매개변수와 비표준 매개변수 간의 충돌(및 그에 따른 상호 운용성 문제)을 방지하는 좋은 방법이었지만, 실제로는 비표준 매개변수가 표준 영역으로 유출됨에 따른 비용이 이점보다 더 컸습니다.

이 문서는 이메일 및 SIP 커뮤니티의 경험을 일반화하여 다음을 수행합니다:

1.  애플리케이션 프로토콜에서 **새로 정의되는 매개변수**에 대해 "X-" 관행을 중단(deprecate)합니다. 이는 기존 프로토콜의 새로운 매개변수에도 적용됩니다. 이 변경은 [RFC822]의 이메일 경우처럼 "X-" 관행이 명시적으로 제공되지 않고 암시적으로만 존재했던 경우에도 적용됩니다.
2.  표준 매개변수와 비표준 매개변수 간의 구분이 없는 환경에서 어떻게 진행해야 하는지에 대한 구체적인 권장 사항을 제시합니다(단, 텍스트 이름을 가진 매개변수에 한하며, 숫자로 표현되는 매개변수는 이 문서의 범위를 벗어납니다).
3.  사설(private), 로컬, 예비(preliminary), 실험적 또는 구현별(implementation-specific) 매개변수의 사용 자체를 반대하는 것이 아니라, 그러한 매개변수의 이름에 "X-" 및 유사한 구조를 사용하는 것을 반대합니다.
4.  기존의 "X-" 매개변수를 계속 사용할지 아니면 "X-"가 없는 형식으로 마이그레이션할지에 대해서는 권장 사항을 제시하지 않습니다. 이는 해당 매개변수의 생성자나 유지 관리자가 결정할 문제입니다.
5.  특정 애플리케이션 프로토콜에 대해 "X-" 사용을 규정하는 기존 사양(예: [[RFC5545]](https://datatracker.ietf.org/doc/html/rfc5545)의 "x-name" 토큰)을 무효화하지 않습니다. 이는 해당 프로토콜의 설계자가 결정할 문제입니다.

이 문서의 "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "NOT RECOMMENDED", "MAY", "OPTIONAL" 키워드는 [[RFC2119]](https://datatracker.ietf.org/doc/html/rfc2119)에 기술된 대로 해석해야 합니다.

## 2. 애플리케이션 프로토콜 구현자를 위한 권장 사항

애플리케이션 프로토콜의 구현은 매개변수 이름에 "X-" 또는 유사한 구조가 존재하거나 부재한다는 이유만으로 매개변수의 상태에 대해 어떠한 가정도 해서는 안 되며(MUST NOT), 자동적인 조치를 취해서도 안 됩니다(MUST NOT).

## 3. 새로운 매개변수 생성자를 위한 권장 사항

애플리케이션 프로토콜의 맥락에서 사용될 새로운 매개변수의 생성자는:

1.  자신이 생성하는 모든 매개변수가 표준화되거나, 공개되거나, 널리 배포되거나, 여러 구현에서 사용될 수 있다고 가정해야 합니다(SHOULD).
2.  현재 사용되지 않는다고 믿을 만한 이유가 있는 의미 있는 매개변수 이름을 사용해야 합니다(SHOULD).
3.  매개변수 이름 앞에 "X-" 또는 유사한 구조를 붙이지 말아야 합니다(SHOULD NOT).

> **참고:** 관련 매개변수 네임스페이스에 생성자와 매개변수 이름을 연관시키는 관행이 있는 경우, 매개변수 이름에 조직의 이름이나 기본 도메인 이름을 포함할 수 있습니다(예시는 [부록 B](#부록-b-분석-analysis) 참조).

## 4. 프로토콜 설계자를 위한 권장 사항

매개변수를 사용하여 확장을 허용하는 새로운 애플리케이션 프로토콜의 설계자는:

1.  잠재적으로 무제한의 값 공간(value-spaces)을 가진 레지스트리를 구축해야 하며(SHOULD), 적절한 경우 영구(permanent) 및 임시(provisional) 레지스트리를 모두 정의해야 합니다.
2.  간단하고 명확한 등록 절차를 정의해야 합니다(SHOULD).
3.  매개변수 이름의 형태와 관계없이 모든 비사설(non-private) 매개변수의 등록을 의무화해야 합니다(SHOULD).
4.  "X-" 접두사 또는 유사한 구조를 가진 매개변수의 등록을 금지해서는 안 됩니다(SHOULD NOT).
5.  "X-" 접두사 또는 유사한 구조를 가진 매개변수가 비표준으로 이해되어야 한다고 규정해서는 안 됩니다(MUST NOT).
6.  "X-" 접두사 또는 유사한 구조가 없는 매개변수가 표준으로 이해되어야 한다고 규정해서는 안 됩니다(MUST NOT).

## 5. 보안 고려 사항 (Security Considerations)

보안에 중요한 매개변수의 상호 운용성 및 마이그레이션 문제는 불필요한 취약점을 초래할 수 있습니다(자세한 논의는 [부록 B](#부록-b-분석-analysis) 참조).

[섹션 2](#2-애플리케이션-프로토콜-구현자를-위한-권장-사항)의 권장 사항에 따른 결과로서, 구현은 매개변수의 이름만 보고 표준 매개변수는 "안전"하고 비표준 매개변수는 "불안전"하다고 가정해서는 안 됩니다(MUST NOT).

## 6. IANA 고려 사항 (IANA Considerations)

이 문서는 다양한 애플리케이션 프로토콜에 대해 현재 시행 중인 등록 절차를 수정하지 않습니다. 그러나 향후 이러한 절차는 이 문서에 정의된 모범 사례를 포함하도록 업데이트될 수 있습니다.

## 7. 감사의 글 (Acknowledgements)

Claudio Allocchio, Adam Barth, Nathaniel Borenstein, Eric Burger, Stuart Cheshire, Al Constanzo, Dave Cridland, Ralph Droms, Martin Duerst, Frank Ellermann, J.D. Falk, Ned Freed, Tony Finch, Randall Gellens, Tony Hansen, Ted Hardie, Joe Hildebrand, Alfred Hoenes, Paul Hoffman, Eric Johnson, Scott Kelly, Scott Kitterman, John Klensin, Graham Klyne, Murray Kucherawy, Eliot Lear, John Levine, Bill McQuillan, Alexey Melnikov, Subramanian Moonesamy, Keith Moore, Ben Niven-Jenkins, Zoltan Ordogh, Tim Petch, Dirk Pranke, Randy Presuhn, Julian Reschke, Dan Romascanu, Doug Royer, Andrew Sullivan, Henry Thompson, Martin Thomson, Matthew Wild, Nicolas Williams, Tim Williams, Mykyta Yevstifeyev, Kurt Zeilenga의 피드백에 감사드립니다.

---

## 부록 A. 배경 (Background)

"X-" 관행의 시작은 1975년 Brian Harvey가 FTP 매개변수와 관련하여 제안한 것에서 찾을 수 있습니다 [[RFC691]](https://datatracker.ietf.org/doc/html/rfc691):

> 따라서 Telnet 인쇄와 비인쇄의 구분을 신경 쓰는 FTP 서버는 SRVR N과 SRVR T를 구현할 수 있습니다. 이상적으로 SRVR 매개변수는 충돌을 피하기 위해 Jon Postel에게 등록되어야 하지만, 두 사이트가 서로 다른 용도로 같은 매개변수를 사용한다고 해서 재앙은 아닙니다. 저는 매개변수가 한 글자 이상이 될 수 있도록 허용하고, 정말로 로컬 특이성(idiosyncracies)을 위해서는 초기 문자 X를 사용할 것을 제안합니다.

이 "X" 접두사는 이후 [[RFC737]](https://datatracker.ietf.org/doc/html/rfc737), [[RFC743]](https://datatracker.ietf.org/doc/html/rfc743), [[RFC775]](https://datatracker.ietf.org/doc/html/rfc775)에서 사용되었습니다. 이 사용법은 [[RFC1123]](https://datatracker.ietf.org/doc/html/rfc1123)에서 다음과 같이 언급되었습니다:

> FTP는 "실험적" 명령을 허용하며, 그 이름은 "X"로 시작합니다. 이러한 명령이 나중에 표준으로 채택되더라도 "X" 형식을 사용하는 기존 구현이 여전히 존재할 수 있습니다.... 모든 FTP 구현은 명령 조회 테이블에 추가 항목을 넣어 두 형식을 동일시함으로써 두 가지 형태의 명령을 모두 인식해야 합니다(SHOULD).

"X-" 관행은 적어도 1982년 [[RFC822]](https://datatracker.ietf.org/doc/html/rfc822) 발행 이후 이메일 헤더 필드에 사용되어 왔으며, 여기서 "Extension-fields"(확장 필드)와 "user-defined-fields"(사용자 정의 필드)를 다음과 같이 구분했습니다:

> "X-"라는 서두 문자열은 Extension-fields의 이름에 절대 사용되지 않습니다. 이는 사용자 정의 필드에 보호된 이름 집합을 제공합니다.

이 규칙은 [[RFC1154]](https://datatracker.ietf.org/doc/html/rfc1154)에서 다음과 같이 재진술되었습니다:

> "X-"로 시작하는 키워드는 구현별 사용을 위해 영구적으로 예약됩니다. 표준 등록 인코딩 키워드는 절대 "X-"로 시작하지 않습니다.

이 관행은 미디어 타입([[RFC2045]](https://datatracker.ietf.org/doc/html/rfc2045), [[RFC2046]](https://datatracker.ietf.org/doc/html/rfc2046), [[RFC2047]](https://datatracker.ietf.org/doc/html/rfc2047)), HTTP 헤더([[RFC2068]](https://datatracker.ietf.org/doc/html/rfc2068), [[RFC2616]](https://datatracker.ietf.org/doc/html/rfc2616)), vCard 매개변수 및 속성([[RFC2426]](https://datatracker.ietf.org/doc/html/rfc2426)), URN([[RFC3406]](https://datatracker.ietf.org/doc/html/rfc3406)), LDAP 필드 이름([[RFC4512]](https://datatracker.ietf.org/doc/html/rfc4512)) 및 기타 애플리케이션 기술에 대한 다양한 사양에서 계속되었습니다.

그러나 이메일 헤더에서 "X-" 접두사의 사용은 1982년 [RFC822] 발행과 2001년 [[RFC2822]](https://datatracker.ietf.org/doc/html/rfc2822) 발행 사이에 "extension-field" 구조와 "user-defined-field" 구조 간의 구분을 제거함으로써 사실상 중단되었습니다(SIP "P-" 헤더와 관련하여 [[RFC3427]](https://datatracker.ietf.org/doc/html/rfc3427)이 [[RFC5727]](https://datatracker.ietf.org/doc/html/rfc5727)에 의해 폐기되었을 때 유사한 변경이 발생했습니다).

이메일 헤더에서 "X-" 문자열을 포함하는 매개변수가 사실상 중단되었음에도 불구하고, 다양한 애플리케이션 프로토콜에서 계속 사용되고 있습니다. 이러한 사용을 유발하는 두 가지 주요 상황은 다음과 같습니다:

1.  성공할 경우 향후 표준화될 가능성이 있는 실험.
2.  구현별 사용 또는 사설 네트워크에서의 로컬 사용만을 목적으로 하여 표준화될 의도가 없는 확장.

이 명명 규칙의 사용은 인터넷 표준 프로세스 [[BCP9]](https://datatracker.ietf.org/doc/html/bcp9)나 IANA 등록 규칙 [[BCP26]](https://datatracker.ietf.org/doc/html/bcp26)에 의해 의무화된 것이 아닙니다. 오히려 이 규칙을 참조하는 각 사양이나 이를 사용하기로 선택한 각 관리 프로세스의 개별적인 선택입니다. 특히 일부 표준 트랙 RFC는 이 규칙을 규범적인 방식으로 해석했습니다(예: [[RFC822]](https://datatracker.ietf.org/doc/html/rfc822) 및 [[RFC5451]](https://datatracker.ietf.org/doc/html/rfc5451)).

## 부록 B. 분석 (Analysis)

"X-" 관행의 주된 문제는 비표준 매개변수가 표준 매개변수의 보호된 공간으로 유출되는 경향이 있어, "X-" 이름에서 표준화된 이름으로 마이그레이션해야 할 필요성을 초래한다는 것입니다. 마이그레이션은 상호 운용성 문제(때로는 보안 문제)를 야기하는데, 이는 구형 구현은 "X-" 이름만 지원하고 신형 구현은 표준화된 이름만 지원할 수 있기 때문입니다. 상호 운용성을 보존하기 위해 신형 구현은 "X-" 이름을 영원히 지원하게 되며, 이는 비표준 이름이 사실상의 표준이 됨을 의미합니다(따라서 애초에 네임스페이스를 표준 및 비표준 영역으로 분리할 필요성을 없애버립니다).

우리는 이미 [부록 A](#부록-a-배경-background)의 [[RFC1123]](https://datatracker.ietf.org/doc/html/rfc1123) 인용문에서 FTP와 관련하여 이 현상을 보았습니다. HTTP 커뮤니티도 "x-gzip" 및 "x-compress" 미디어 타입과 관련하여 동일한 경험을 했으며, 이는 [[RFC2068]](https://datatracker.ietf.org/doc/html/rfc2068)에 언급되어 있습니다:

> 이전 HTTP 구현과의 호환성을 위해 애플리케이션은 "x-gzip" 및 "x-compress"를 각각 "gzip" 및 "compress"와 동등한 것으로 간주해야 합니다.

유사한 예는 [[RFC5064]](https://datatracker.ietf.org/doc/html/rfc5064)에서 찾을 수 있는데, 여기서는 "Archived-At" 메시지 헤더 필드를 정의했지만 "X-Archived-At" 필드도 정의하고 등록해야 했습니다:

> 하위 호환성을 위해 이 문서는 Archived-At 헤더 필드의 전신인 X-Archived-At 헤더 필드도 설명합니다. X-Archived-At 헤더 필드는 파싱될 수 있지만(MAY), 생성되어서는 안 됩니다(SHOULD NOT).

네임스페이스를 표준 및 비표준 영역으로 분리한 원래 이유 중 하나는 이름 등록의 어려움 때문이라고 인식되었습니다. 그러나 그 문제에 대한 해결책은 [[RFC3864]](https://datatracker.ietf.org/doc/html/rfc3864) 및 [[RFC4288]](https://datatracker.ietf.org/doc/html/rfc4288)에서 제공하는 것과 같은 더 간단한 등록 규칙이었습니다. [RFC4288]에서 설명한 바와 같이:

> 벤더 및 개인 트리를 위해 위에서 설명한 간소화된 등록 절차를 통해 등록되지 않은 실험적 타입을 사용해야 할 필요성은 거의 없을 것입니다. 따라서 "x-" 및 "x." 형식의 사용은 권장되지 않습니다.

일부 네임스페이스의 경우, [[RFC4395]](https://datatracker.ietf.org/doc/html/rfc4395)에서와 같이 영구 이름과 임시 이름을 위한 별도의 레지스트리를 구축하는 것이 또 다른 유용한 관행이었습니다.

또한 비표준 매개변수의 표준화는 종종 미묘하게 다른 동작을 초래합니다(예: 표준화 과정에서 제공된 보안 검토 결과로 인해 표준화된 버전이 다른 보안 속성을 가질 수 있음). 구현자가 이전의 비표준 매개변수와 새로운 표준 매개변수를 동등하게 취급하면 상호 운용성 및 보안 문제가 발생할 수 있습니다. 결함을 감지하고 수정하기 위한 비표준 매개변수의 분석은 일반적으로 좋은 일이며, 요소 이름에 구분이 없다고 해서 이를 저지하려는 의도는 아닙니다. 원래 비표준 매개변수나 프로토콜 요소가 표준화되고 새로운 형식이 상호 운용성이나 보안 속성에 영향을 미치는 차이점을 갖는다면, 구현이 이전 형식을 새로운 형식과 동일하게 취급하는 것은 부적절할 것입니다. 세션 개시 프로토콜(SIP)의 "P-" 관행과 관련된 유사한 고려 사항은 [[RFC5727]](https://datatracker.ietf.org/doc/html/rfc5727)을 참조하십시오.

특정 애플리케이션 프로토콜에서 매개변수 네임스페이스를 분리하는 것이 정당화될 수 있는 상황도 있습니다:

1.  일부 매개변수가 표준화될 가능성이 극히 낮은 경우. 이 경우 구현별 및 사설 사용 매개변수는 적어도 조직의 이름(예: "ExampleInc-foo" 또는 [[RFC4288]](https://datatracker.ietf.org/doc/html/rfc4288)과 일치하는 "VND.ExampleInc.foo")이나 기본 도메인 이름(예: "com.example.foo" 또는 "http://example.com/foo"와 같은 URI [[RFC3986]](https://datatracker.ietf.org/doc/html/rfc3986))을 포함할 수 있습니다. 드문 경우지만 진정한 실험적 매개변수에는 무의미한 단어, 해시 함수의 출력 또는 UUID(Universally Unique Identifiers) [[RFC4122]](https://datatracker.ietf.org/doc/html/rfc4122)와 같은 의미 없는 이름이 부여될 수 있습니다.
2.  매개변수 이름이 중요한 의미를 가질 수 있는 경우. 구현자는 거의 항상 기존 용어에 대한 동의어(예: "priority" 대신 "urgency")를 찾거나 단순히 더 창의적인 이름(예: "get-it-there-fast")을 발명할 수 있으므로 이 경우도 드뭅니다. 유사한 이름의 매개변수가 여러 개 존재하면 혼란스러울 수 있지만, 이는 표준 및 비표준 매개변수를 분리하려는 시도가 있더라도 마찬가지입니다(예: "X-Priority"는 "Urgency"와 혼동될 수 있음).
3.  매개변수 이름이 매우 짧아야 하는 경우(예: 언어 태그에 대한 [[RFC5646]](https://datatracker.ietf.org/doc/html/rfc5646)). 이 경우 사람이 읽을 수 있는 이름 대신 숫자를 할당하고(예: DHCP 옵션에 대한 [[RFC2939]](https://datatracker.ietf.org/doc/html/rfc2939)), 구현별 확장이나 사설 사용을 위해 특정 숫자 범위를 남겨두는 것이 더 효율적일 수 있습니다(예: 세션 기술 프로토콜(SDP) [[RFC4566]](https://datatracker.ietf.org/doc/html/rfc4566)과 함께 사용되는 코덱 번호).

애플리케이션 프로토콜의 모범 사례로서 "X-" 관행을 중단하는 것에 대해 세 가지 주요 반대 의견이 있습니다:

1.  구현자가 하나의 매개변수를 유사한 이름을 가진 다른 매개변수로 오인할 수 있습니다. "X-" 접두사와 같은 엄격한 구분은 이를 명확하게 할 수 있습니다. 그러나 실제로는 구현자가 구분을 모호하게 하도록 강요받으므로(예: "X-foo"를 사실상의 표준으로 취급), 이는 필연적으로 무의미해집니다.
2.  충돌은 바람직하지 않으며, 표준 매개변수 "foo"와 비표준 매개변수 "foo"가 동시에 존재하는 것은 좋지 않습니다. 그러나 이름은 거의 항상 저렴하므로 "foo"라는 실험적, 구현별 또는 사설 사용 이름이 표준 개발 기구에서 "bar"와 같이 유사하게 창의적인 이름을 발행하는 것을 막지는 못합니다.
3.  [[BCP82]](https://datatracker.ietf.org/doc/html/bcp82)의 제목은 "실험 및 테스트 번호 할당이 유용한 것으로 간주됨"이며, 따라서 "X-" 접두사도 실험적 매개변수에 유용함을 암시합니다. 그러나 [[BCP 82]](https://datatracker.ietf.org/doc/html/bcp82)는 해당 번호의 풀이 엄격하게 제한된 경우(예: DHCP 옵션)나 순수하게 실험적인 목적이라도 번호가 절대적으로 필요한 경우(예: IP 헤더의 프로토콜 필드)에 프로토콜 번호의 필요성을 다룹니다. 프로토콜 매개변수를 사용하는 거의 모든 애플리케이션 프로토콜(이메일 헤더, 미디어 타입, HTTP 헤더, vCard 매개변수 및 속성, URN, LDAP 필드 이름 포함)에서 네임스페이스는 어떤 방식으로든 제한되거나 제약되지 않으므로, 사설 사용이나 실험적 목적을 위해 이름 블록을 할당할 필요가 없습니다([[BCP26]](https://datatracker.ietf.org/doc/html/bcp26) 참조).

따라서 매개변수 공간을 표준화된 영역과 비표준 영역으로 분리하는 것은 이점이 거의 없으며 상호 운용성 측면에서 적어도 하나의 상당한 비용이 발생하는 것으로 보입니다.

---
*참고: 이 번역은 원본 RFC 6648의 내용을 바탕으로 한국어 사용자를 위해 정리된 것입니다. 정확한 기술적 해석을 위해서는 원본 문서를 참조하시기 바랍니다.*
