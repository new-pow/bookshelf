from movies import Movies
from jaccard_similarity_cal import MovieSimilarityCalculator

movie_data = Movies()
cal = MovieSimilarityCalculator(movie_data) # data/movies.csv 파일을 읽어서 Movies 객체 생성
top_similar_movies = cal.get_topk_jaccard_genres(target_mid=112552, k=10) # target_mid=1인 영화와 비슷한 영화 10개 추출

for mid, (title, similarity) in top_similar_movies:
    print(f"Movie ID: {mid}, Title: {title}, Similarity: {similarity:.2f}")

# 정확도가 딱히 높은거 같지는 않음.
# 이것을 여러개 중첩해서 사용하면 더 정확한 추천을 할 수 있을 것 같음.

# 그래비티
# Movie ID: 110553, Title: The Amazing Spider-Man 2 (2014), Similarity: 1.00
# Movie ID: 111759, Title: Edge of Tomorrow (2014), Similarity: 1.00
# Movie ID: 5378, Title: Star Wars: Episode II - Attack of the Clones (2002), Similarity: 0.75
# Movie ID: 8636, Title: Spider-Man 2 (2004), Similarity: 0.75
# Movie ID: 44191, Title: V for Vendetta (2006), Similarity: 0.75
# Movie ID: 46530, Title: Superman Returns (2006), Similarity: 0.75
# Movie ID: 53996, Title: Transformers (2007), Similarity: 0.75
# Movie ID: 59037, Title: Speed Racer (2008), Similarity: 0.75
# Movie ID: 68358, Title: Star Trek (2009), Similarity: 0.75
# Movie ID: 69526, Title: Transformers: Revenge of the Fallen (2009), Similarity: 0.75

# 라라랜드
# Movie ID: 4, Title: Waiting to Exhale (1995), Similarity: 1.00
# Movie ID: 11, Title: American President, The (1995), Similarity: 1.00
# Movie ID: 52, Title: Mighty Aphrodite (1995), Similarity: 1.00
# Movie ID: 58, Title: Postman, The (Postino, Il) (1994), Similarity: 1.00
# Movie ID: 94, Title: Beautiful Girls (1996), Similarity: 1.00
# Movie ID: 195, Title: Something to Talk About (1995), Similarity: 1.00
# Movie ID: 224, Title: Don Juan DeMarco (1995), Similarity: 1.00
# Movie ID: 232, Title: Eat Drink Man Woman (Yin shi nan nu) (1994), Similarity: 1.00
# Movie ID: 281, Title: Nobody's Fool (1994), Similarity: 1.00
# Movie ID: 351, Title: Corrina, Corrina (1994), Similarity: 1.00