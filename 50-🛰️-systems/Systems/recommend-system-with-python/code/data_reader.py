from collections import defaultdict
import csv

class Movies:
    def __init__(self):
        self.movies = {}  # movie_id -> movie title
        self.genresets = {}  # movie_id -> set of genres

        with open('./data/movies.csv', newline='') as csvfile:
            csvreader = csv.reader(csvfile)
            next(csvreader)  # Skip header

            for mid, title, genres in csvreader:
                self.movies[int(mid)] = title
                self.genresets[int(mid)] = set(genres.split('|'))

    def get_movie_title(self, movie_id):
        return self.movies.get(movie_id)

    def get_movie_genres(self, movie_id):
        return self.genresets.get(movie_id)

class Ratings:
    def __init__(self):
        self.usets = defaultdict(set)  # user_id -> set of movie_id} # 비어있다면 빈 set을 반환
        self.ratings = []
        
        with open('./data/ratings.csv', newline='') as csvfile:
            csvreader = csv.reader(csvfile)
            next(csvreader)  # Skip header

            for line in csvreader:
                user_id = int(line[0])
                movie_id = int(line[1])
                rating = float(line[2])
                self.ratings.append((user_id, movie_id, rating))
                
                # 사용자가 평가한 영화 목록을 만듦
                self.usets[movie_id].add(user_id)
                
                if (self.ratings.__sizeof__() > 100000): # 편의를 위해 100000개까지만 읽어옴
                    break
                
    