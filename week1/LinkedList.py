class Node:
    def __init__(self, data):
        self.data = data
        self.next = None


class LinkedList:
    def __init__(self):
        self.head = None
        self.length = 0

    def __len__(self):
        return self.length

    def appendleft(self, data):
        if self.head is None:
            self.head = Node(data)
        else:
            node = Node(data)
            node.next = self.head
            self.head = node
        self.length += 1

    def append(self, data):
        node = self.head
        if self.head is None:
            self.head = Node(data)
        else:
            while node.next is not None:
                node = node.next
            node.next = Node(data)
        self.length += 1

    def __str__(self):
        if self.head is None:
            return "Linked list is empty"
        res = "Head"
        node = self.head
        while node is not None:
            res += " -> " + str(node.data)
            node = node.next
        return res

    def __contains__(self, target):
        if self.head is None:
            return False
        node = self.head
        while node is not None:
            if node.data == target:
                return True
            node = node.next
        return False

    def popleft(self):
        if self.head is None:
            return None
        node = self.head
        self.head = self.head.next
        self.length -= 1
        return node.data

    def pop(self):
        if self.head is None:
            return None
        node = self.head
        while node.next is not None:
            prev = node
            node = node.next
        if node == self.head:
            self.head = None
        else:
            prev.next = None
        self.length -= 1
        return node.data

    def remove(self, target):
        if self.head is None:
            return False
        node = self.head
        while node is not None and node.data != target:
            prev = node
            node = node.next
        if node is None:
            return False
        if node == self.head:
            self.head = self.head.next
        else:
            prev.next = node.next
        self.length -= 1
        return True

    def insert(self, i, data):
        if i <= 0:
            self.appendleft(data)
        elif i > self.length:
            self.append(data)
        else:
            node = self.head
            for _ in range(i - 1):
                node = node.next
            new_node = Node(data)
            new_node.next = node.next
            node.next = new_node
            self.length += 1

if __name__ == "__main__":
    ll = LinkedList()

    # 1. 맨 앞, 맨 뒤 삽입(appendleft, append)
    ll.append(20)
    ll.appendleft(10)
    print("1. 삽입 결과:", ll)  # Head -> 10 -> 20

    # 2. 값 존재 확인(__contains__)
    print("2. 탐색(10 존재 여부):", 10 in ll)  # True

    # 3. 중간 위치 삽입(insert)
    ll.insert(1, 15)  # 인덱스 1에 15 삽입
    print("3. insert(1, 15) 결과:", ll)  # Head -> 10 -> 15 -> 20

    # 4. 맨 앞, 맨 뒤 삭제(pop, popleft)
    left_val = ll.popleft()  # 맨 앞(10) 제거
    right_val = ll.pop()  # 맨 뒤(20) 제거
    print(f"4. popleft({left_val}), pop({right_val}) 후:", ll)  # Head -> 15

    # 5. 값 기반 삭제(remove)
    removed = ll.remove(15)
    print("5. remove(15) 성공 여부:", removed)  # True
    print("   최종 리스트 상태:", ll)  # Linked list is empty