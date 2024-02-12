# 왜 Array Index는 0부터 시작하는 것일까?
- 해당 의문에 관련된 좋았던 글과 그에 대한 생각을 모아두었습니다.

- [Why numbering should start at zero](https://www.cs.utexas.edu/users/EWD/transcriptions/EWD08xx/EWD831.html)
	- 수열을 표시할 때, 왜 0부터 시작하는 것이 좋은지 서술하는 글입니다.
	- 어떻게 수열을 표시할 수 있는지 예시를 제시하는 것을 시작으로, 글쓴이는 결국 `0<=i<N` 로표시하는 편이 알아보기 좋다는 결론에 이릅니다.
	- `So let us let our ordinals start at zero: an element's ordinal (subscript) equals the number of elements preceding it in the sequence. And the moral of the story is that we had better regard —after all those centuries!— zero as a most natural number.`
- [Why The Array Index Should Start From 0](https://developeronline.blogspot.com/2008/04/why-array-index-should-start-from-0.html)
	- 왜 C/C++에서 index를 0부터 시작했는지에 대한 글입니다. (C/C++을 근거로하여 Java 등 다양한 언어에서도 0부터 인덱스를 시작하길 채택하였습니다.)
	- 결론은 Index란 디스크 메모리와 긴밀한 연관성을 가지고 있습니다. 배열의 시작요소부터 n 요소 떨어진 메모리 위치를 뜻하는 것이 index이기 때문입니다. 따라서 시작지점의 요소는 `array[0]` 로 표시할 수 있습니다.
	- 더불어 