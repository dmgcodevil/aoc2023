from collections import deque

if __name__ == '__main__':
    with open('input', 'r') as file:
        lines = file.readlines()
        n = len(lines)
        wins = [0 for _ in range(n)]
        for i, line in enumerate(lines):
            count = 0
            nums = line.strip().split(':')[1].split('|')
            win_nums = set(nums[0].split(' '))
            card_nums = set(nums[1].split(' '))
            win_nums.remove('')
            card_nums.remove('')
            for card_num in card_nums:
                if card_num in win_nums:
                    count = count + 1
            wins[i] = count
        result = [0 for _ in range(n)]
        stack = deque()
        for i in range(n - 1, -1, -1):
            stack.append(i)
        while len(stack) > 0:
            card = stack.pop()
            result[card] = result[card] + 1
            # print(f"card-{card+1} won {wins[card]} cards")
            for i in range(card + wins[card], card, -1):
                # print(f"add {i+1} card to stack")
                stack.append(i)
        # for i, x in enumerate(result):
        #     print(f"card{i + 1} = {x}")
        print(f"answer={sum(result)}")
