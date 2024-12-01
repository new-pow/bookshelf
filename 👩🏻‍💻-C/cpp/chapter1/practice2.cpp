#include <iostream>
#include <map>
#include <string>
#include <vector>
#include <algorithm>
#include <random>
#include <thread>

// 구조체 선언
struct Card {
    int number;
};

struct CardHolder {
    std::vector<Card> cards;
};

struct PlayerBoard {
    CardHolder holder;
};

struct Score {
    int win = 0;
    int lost = 0;
};

struct Player {
    std::string id;
    std::string name;
    PlayerBoard playerBoard;
    Score score;
};

// 함수 프로토타입 선언
// 패키지화 시켜서 사용가능 -> header file로 빼서 사용
std::map<std::string, Player> assignPlayers(int size);
std::string askPlayerName(int index);
std::string generateRandomID(size_t length = 12); // 사이즈 타입 ?!?!? 신기하네
void printPlayerInfo(const std::map<std::string, Player>& players);
void shuffleCards(std::vector<Card>& cards);
void playGame(std::map<std::string, Player>& players);
void initBoard(Player& player);
bool playRound(std::map<std::string, Player>& players);

int main() {
    std::map<std::string, Player> players = assignPlayers(4);

    std::cout << "플레이어 정보를 모두 입력했습니다.\n";
    printPlayerInfo(players);

    playGame(players);
    printPlayerInfo(players);
}

// 라운드 플레이 함수
bool playRound(std::map<std::string, Player>& players) {
    std::map<std::string, Card> selectedCards;
    std::map<int, int> cardCount; // 카드 번호별 뽑힌 횟수를 저장

    // 1. 플레이어가 각각 무작위로 카드를 뽑음
    for (auto& player : players) { // auto$ 타입 추론
        if (player.second.playerBoard.holder.cards.empty()) {
            std::cout << player.second.name << "님의 카드가 모두 소진되었습니다." << std::endl;
            player.second.score.lost++;
            return false;
        }
        Card card = player.second.playerBoard.holder.cards.back(); // 뒤에서부터 카드 뽑기
        player.second.playerBoard.holder.cards.pop_back();
        selectedCards[player.first] = card; // 뽑은 카드 저장
        std::cout << player.second.name << "님이 " << card.number << "번 카드를 뽑았습니다." << std::endl;
        cardCount[card.number]++;
        std::this_thread::sleep_for(std::chrono::seconds(1)); // 스레드를 나누는 방법 알아봐도 좋을듯! << 다음에 알아올게용!
    }

    // 2. 중복된 카드 제거
    std::vector<std::string> duplicatePlayers;
    for (const auto& [playerId, card] : selectedCards) {
        if (cardCount[card.number] > 1) {
            std::cout << "카드 번호 " << card.number << "는 중복되었습니다. " 
                      << players[playerId].name << "님의 카드를 제거합니다." << std::endl;
            duplicatePlayers.push_back(playerId); // 중복 플레이어 ID 저장
            players[playerId].score.lost++;
        }
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }
    for (const auto& playerId : duplicatePlayers) {
        selectedCards.erase(playerId);
    }

    // 3. 숫자가 더 높은 플레이어가 승리
    std::string winnerId;
    int maxNumber = -1;
    for (const auto& [playerId, card] : selectedCards) {
        if (card.number > maxNumber) {
            maxNumber = card.number;
            winnerId = playerId;
        }
    }

    // 4. 승리한 플레이어는 승리 횟수를 증가
    if (!winnerId.empty()) {
        std::cout << "라운드에서 " << players[winnerId].name << "님이 카드번호 "<< maxNumber << " 로 승리했습니다." << std::endl;
        players[winnerId].score.win++;
        std::this_thread::sleep_for(std::chrono::seconds(2));
    }

    // 5. 라운드 종료 후 플레이어 정보 출력
    printPlayerInfo(players);

    return true;
}

// 게임 플레이 함수
void playGame(std::map<std::string, Player>& players) {
    int round = 1;
    bool hasNextRound = true;

    for (auto& player : players) { 
        std::cout << player.second.name << "님의 보드를 초기화합니다." << std::endl;
        initBoard(player.second);   
    }

    while (hasNextRound) {
        std::cout << "라운드 " << round << "을 시작합니다." << std::endl;
        std::this_thread::sleep_for(std::chrono::seconds(2));
        hasNextRound = playRound(players);
        round++;
    }

    std::cout << "게임이 종료되었습니다." << std::endl;
}

void initBoard(Player& player) {
    player.playerBoard.holder.cards.clear();
    for (int i = 0; i < 13; i++) {
        Card card;
        card.number = i;
        player.playerBoard.holder.cards.push_back(card);
    }
    shuffleCards(player.playerBoard.holder.cards);
}

// 플레이어 정보 출력 함수
void printPlayerInfo(const std::map<std::string, Player>& players) {
    for (const auto& player : players) {
        std::cout << "플레이어 이름: " << player.second.name << std::endl;
        std::cout << "플레이어 ID: " << player.second.id << std::endl;
        std::cout << "플레이어 점수 (승/패): " << player.second.score.win << "/" << player.second.score.lost << std::endl;
        std::cout << "----" << std::endl;
    }
}

// 플레이어 할당 함수
std::map<std::string, Player> assignPlayers(int size) {
    std::map<std::string, Player> players;
    for (int i = 0; i < size; i++) {
        std::string requestedName = askPlayerName(i);

        Player player;
        player.name = requestedName;

        std::string uniqueID;
        do {
            uniqueID = generateRandomID();
        } while (players.find(uniqueID) != players.end());
        
        player.id = uniqueID;
        players[player.id] = player;
    }
    return players;
}

// 플레이어 이름 요청 함수
std::string askPlayerName(int index) {
    std::string name;
    std::cout << "유저 " << index + 1 << " 이름을 입력해주세요: ";
    std::cin >> name;
    return name;
}

// 카드 섞기 함수
void shuffleCards(std::vector<Card>& cards) {
    std::random_device rd;
    std::mt19937 g(rd());
    std::shuffle(cards.begin(), cards.end(), g);
}

// 랜덤 ID 생성 함수
std::string generateRandomID(size_t length) {
    const std::string chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, chars.size() - 1);

    std::string randomID;
    for (size_t i = 0; i < length; ++i) {
        randomID += chars[dis(gen)];
    }
    return randomID;
}
