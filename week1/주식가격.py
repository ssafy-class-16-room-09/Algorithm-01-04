def solution(prices):
    answer = []
    p_len = len(prices)
    
    for i in range(p_len-1):
        count = 0
        for j in range(i+1, p_len):
            count += 1
            if prices[i] > prices[j]:
                break
        answer.append(count)
    
    # 어차피 맨 마지막은 0일테니(비교군 없어서)
    answer.append(0)
    return answer