from collections import deque

for i in range(1, 11):
    T = int(input())

    queue_data = deque(map(int, input().split()))

    num = 1
    while True:
        target = queue_data.popleft()
        queue_data.append(target - num)
        if (target - num) <= 0:
            queue_data[7] = 0
            break
        if num == 5:
            num = 0
        num += 1

    print(f"#{i}", end=" ")
    for _ in range(8):
        q = queue_data.popleft()
        print(q, end=" ")
    print()