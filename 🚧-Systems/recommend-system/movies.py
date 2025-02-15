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
