document.addEventListener("DOMContentLoaded", function() { // DOM이 로드되면 실행
    let lazyImages = [].slice.call(document.querySelectorAll("img.lazy")) // lazy 클래스를 가진 img 요소를 모두 가져와 배열로 만든다.
    let active = false
    const lazyLoad = function() { // lazyLoad 함수를 선언   
      if (active === false) {
        active = true 
        setTimeout(()=> {  // setTimeout을 사용하여 200ms의 딜레이를 준다.
          lazyImages = lazyImages.map((lazyImage) => { // lazyImages 배열을 순회하며 lazyImage를 가져온다.
            if (lazyImage.getBoundingClientRect().top <= window.innerHeight && window.getComputedStyle(lazyImage).display !== "none") {   // lazyImage가 화면에 보이는지 확인
              lazyImage.src = lazyImage.dataset.src // lazyImage의 src에 data-src 값을 할당
              lazyImage.classList.remove("lazy")  // lazyImage의 클래스에서 lazy를 제거
              return null
            }else return lazyImage // lazyImage가 화면에 보이지 않으면 lazyImage를 반환
          }).filter(image => image) // lazyImage가 null이 아닌 것만 필터링
          if (!lazyImages.length) {
            document.removeEventListener("scroll", lazyLoad) // lazyImages 배열이 비어있으면 scroll 이벤트 제거
          }else active = false // lazyImages 배열이 비어있지 않으면 active를 false로 변경
        }, 200)
      }
    }
    document.addEventListener("scroll", lazyLoad) // scroll 이벤트가 발생하면 lazyLoad 함수 실행
  });