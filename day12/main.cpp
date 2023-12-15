#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <unordered_map>

long count(const std::string& s, int i, int j, int len, const std::vector<int>& group, std::unordered_map<std::string, long>& cache) {
    if (j == group.size() || (j == group.size() - 1 && group[j] == len)) {
        return (s.length() == i || s.find("#", i) == std::string::npos) ? 1 : 0;
    }

    if (i == s.length()) return 0;
    std::string key = std::to_string(i) + "-" + std::to_string(len) + "-" + std::to_string(j);
    if (cache.find(key) != cache.end()) return cache[key];

    long res = 0;
    if (len == group[j] && (s[i] == '.' || s[i] == '?')) {
        res = count(s, i + 1, j + 1, 0, group, cache);
    } else if (len == 0) {
        if (s[i] == '?') {
            long c1 = count(s, i + 1, j, 1, group, cache);
            long c2 = count(s, i + 1, j, 0, group, cache);
            res = c1 + c2;
        } else if (s[i] == '#') {
            res = count(s, i + 1, j, 1, group, cache);
        } else {
            res = count(s, i + 1, j, 0, group, cache);
        }
    } else if (s[i] == '#' || s[i] == '?') {
        res = count(s, i + 1, j, len + 1, group, cache);
    }

    cache[key] = res;
    return res;
}

int main(int argc, const char * argv[]) {
    
    std::string filename = "input";
        std::ifstream file(filename);
    if (!file.is_open()) {
            std::cerr << "Failed to open file" << std::endl;
            return 1;
        }
    
        std::string line;
        long answer = 0;
        while (std::getline(file, line)) {
            std::istringstream iss(line);
            std::string part, groupStr;
            iss >> part >> groupStr;

            std::istringstream groupStream(groupStr);
            std::vector<int> groups;
            std::string groupSize;
            while (std::getline(groupStream, groupSize, ',')) {
                groups.push_back(std::stoi(groupSize));
            }

            std::string s = part;
            for (int i = 1; i < 5; ++i) {
                s += "?";
                s += part;
            }

            std::vector<int> expandedGroups = groups;
            for (int i = 1; i < 5; ++i) {
                expandedGroups.insert(expandedGroups.end(), groups.begin(), groups.end());
            }

            std::unordered_map<std::string, long> cache;
            long c = count(s, 0, 0, 0, expandedGroups, cache);
            answer += c;
            std::cout << line << " = " << c << std::endl;
        }

        std::cout << "answer = " << answer << std::endl;

        return 0;
}
