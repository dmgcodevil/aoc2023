if __name__ == '__main__':
    with open('input', 'r') as file:
        lines = file.readlines()
        total = 0
        for line in lines:
            points = 0
            points_sum = 0
            nums = line.strip().split(':')[1].split('|')
            win_nums = set(nums[0].split(' '))
            card_nums = set(nums[1].split(' '))
            win_nums.remove('')
            card_nums.remove('')
            for card_num in card_nums:
                if card_num in win_nums:
                    points_sum = points_sum + points
                    points = max(1, points * 2)
            total = total + points
    print(f"answer={total}")
