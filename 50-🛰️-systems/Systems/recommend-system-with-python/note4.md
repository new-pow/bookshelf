---
tags: [systems, distributed-systems]
---

# Surprise 패키지 사용
- `pip install scikit-surprise` 
	- 아래와 같은 에러가 발생함.
```
(venv) ☁  recommend-system [main] ⚡  pip install scikit-surprise                                 

Collecting scikit-surprise
  Using cached scikit_surprise-1.1.4.tar.gz (154 kB)
  Installing build dependencies ... done
  Getting requirements to build wheel ... error
  error: subprocess-exited-with-error
  
  × Getting requirements to build wheel did not run successfully.
  │ exit code: 1
  ╰─> [45 lines of output]
      
      Error compiling Cython file:
      ------------------------------------------------------------
      ...
              self.avg_cltr_i = avg_cltr_i
              self.avg_cocltr = avg_cocltr
      
              return self
      
          def compute_averages(self, np.ndarray[np.int_t] cltr_u,
                                                   ^
      ------------------------------------------------------------
      
      surprise/prediction_algorithms/co_clustering.pyx:157:45: Invalid type.
      Compiling surprise/similarities.pyx because it changed.
      Compiling surprise/prediction_algorithms/matrix_factorization.pyx because it changed.
      Compiling surprise/prediction_algorithms/optimize_baselines.pyx because it changed.
      Compiling surprise/prediction_algorithms/slope_one.pyx because it changed.
      Compiling surprise/prediction_algorithms/co_clustering.pyx because it changed.
      [1/5] Cythonizing surprise/prediction_algorithms/co_clustering.pyx
      Traceback (most recent call last):
        File "/Users/berapt/Study/playground/recommend-system/path/to/venv/lib/python3.13/site-packages/pip/_vendor/pyproject_hooks/_in_process/_in_process.py", line 389, in <module>
          main()
          ~~~~^^
        File "/Users/berapt/Study/playground/recommend-system/path/to/venv/lib/python3.13/site-packages/pip/_vendor/pyproject_hooks/_in_process/_in_process.py", line 373, in main
          json_out["return_val"] = hook(**hook_input["kwargs"])
                                   ~~~~^^^^^^^^^^^^^^^^^^^^^^^^
        File "/Users/berapt/Study/playground/recommend-system/path/to/venv/lib/python3.13/site-packages/pip/_vendor/pyproject_hooks/_in_process/_in_process.py", line 143, in get_requires_for_build_wheel
          return hook(config_settings)
        File "/private/var/folders/mm/rrlm0mvs1ggfmykhb7y7_h100000gn/T/pip-build-env-bxglkj8b/overlay/lib/python3.13/site-packages/setuptools/build_meta.py", line 334, in get_requires_for_build_wheel
          return self._get_build_requires(config_settings, requirements=[])
                 ~~~~~~~~~~~~~~~~~~~~~~~~^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        File "/private/var/folders/mm/rrlm0mvs1ggfmykhb7y7_h100000gn/T/pip-build-env-bxglkj8b/overlay/lib/python3.13/site-packages/setuptools/build_meta.py", line 304, in _get_build_requires
          self.run_setup()
          ~~~~~~~~~~~~~~^^
        File "/private/var/folders/mm/rrlm0mvs1ggfmykhb7y7_h100000gn/T/pip-build-env-bxglkj8b/overlay/lib/python3.13/site-packages/setuptools/build_meta.py", line 320, in run_setup
          exec(code, locals())
          ~~~~^^^^^^^^^^^^^^^^
        File "<string>", line 116, in <module>
        File "/private/var/folders/mm/rrlm0mvs1ggfmykhb7y7_h100000gn/T/pip-build-env-bxglkj8b/overlay/lib/python3.13/site-packages/Cython/Build/Dependencies.py", line 1154, in cythonize
          cythonize_one(*args)
          ~~~~~~~~~~~~~^^^^^^^
        File "/private/var/folders/mm/rrlm0mvs1ggfmykhb7y7_h100000gn/T/pip-build-env-bxglkj8b/overlay/lib/python3.13/site-packages/Cython/Build/Dependencies.py", line 1321, in cythonize_one
          raise CompileError(None, pyx_file)
      Cython.Compiler.Errors.CompileError: surprise/prediction_algorithms/co_clustering.pyx
      [end of output]
  
  note: This error originates from a subprocess, and is likely not a problem with pip.
error: subprocess-exited-with-error

× Getting requirements to build wheel did not run successfully.
│ exit code: 1
╰─> See above for output.

note: This error originates from a subprocess, and is likely not a problem with pip.
```
- `pip install --verbose scikit-learn` 로 해결함
- `pip install --pre --extra-index https://pypi.anaconda.org/scipy-wheels-nightly/simple scikit-learn`

## 기본 활용 방법
- 데이터
	- ml-100k
	- ml-1m
	- jester 
- 알고리즘

| 알고리즘                            | 설명                                                                                  |
| ------------------------------- | ----------------------------------------------------------------------------------- |
| **random_pred.NormalPredictor** | Training set의 분포가 정규분포라고 가정한 상태에서 평점을 무작위로 추출하는 알고리즘. 일반적으로 성능이 안 좋음                |
| **Baseline_only.BaselineOnly**  | 사용자의 평점 평균과 아이템의 평점 평균을 모델화해서 예측하는 알고리즘                                             |
| **knns.KNNBasic**               | 3.4번째 강의에서 소개한 집단을 고려한 기본적인 CF 알고리즘                                                 |
| **knns.KNNWithMeans**           | 3.6번째 강의에서 소개한 사용자의 평가 경향을 고려한 CF 알고리즘                                              |
| **knns.KNNWithZScore**          | 사용자의 평가 경향을 표준(정규분포)화시킨 CF 알고리즘                                                     |
| **matrix_factorization.SVD**    | 4.4번째 강의에서 설명한 MF 알고리즘                                                              |
| **knns.KNNBaseline**            | 사용자의 평점 평균과 아이템의 평점 평균을 모델화시킨 것(Baseline rating)을 고려한 CF 알고리즘                       |
| **matrix_factorization.SVDpp**  | MF를 기반으로 사용자의 특정 아이템에 대한 평가 여부를 인지값으로 인풋하여 암묵적 평가(implicit ratings)를 추가한 SVD++ 알고리즘 |
| **matrix_factorization.NMF**    | 행렬의 값이 전부 양수일 때 사용 가능한 MF 알고리즘                                                      |
| **slope_one.SlopeOne**          | 간단하면서도 정확도가 높은 것이 특징인 SlopeOne 알고리즘을 적용한 Item-based CF 알고리즘                         |
| **co_clustering.CoClustering**  | 사용자와 아이템을 동시에 클러스터링하는 기법을 적용한 CF 알고리즘                                               |

## 알고리즘의 비교
- BaselineOnly
- KNNWithMeans
	- 사용자 평가경향 고려한 CF
- SVD
	- MF 기반 알고리즘
- SVDpp
	- MF 기반 사용자의 특정 아이템에 대한 평가여부를 이진값으로 암묵적 평가까지 추가된 모델

## 유사도 지표 종류
![](https://i.imgur.com/b0KE3ti.png)
- msd
	- 기본
- cosine sim
	- 교집합 안에서만 비교
- pearson sim
	- 두 벡터에 상관 계수를 말함
	- 사용자의 평균을 제거해줌. 상대적인 변수를 제거
- pearson baseline sim
	- 베이스라인 예측값을 제거해줌.

