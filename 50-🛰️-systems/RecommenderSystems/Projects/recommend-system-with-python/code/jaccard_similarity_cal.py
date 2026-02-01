class MovieSimilarityCalculator:
    def __init__(self, movies):
        self.movies = movies  # Movies 객체를 주입받음

    def jaccard_similarity(self, a, b):
        if len(a) == 0 or len(b) == 0:
            return 0
        return len(a & b) / len(a | b)

    def get_topk_jaccard_genres(self, target_mid, k=20):
        target_genres = self.movies.get_movie_genres(target_mid)
        if target_genres is None:
            return []

        similarities = {}
        for mid, genres in self.movies.genresets.items():
            if mid == target_mid:
                continue
            title = self.movies.get_movie_title(mid)
            similarity = self.jaccard_similarity(target_genres, genres)
            similarities[mid] = (title, similarity)

        return sorted(similarities.items(), key=lambda x: x[1][1], reverse=True)[:k]
