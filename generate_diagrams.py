import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches

os.makedirs("docs/images", exist_ok=True)

# Set global styles
plt.rcParams['font.sans-serif'] = 'Arial'
plt.rcParams['font.family'] = 'sans-serif'

# 1. System Architecture Diagram
fig, ax = plt.subplots(figsize=(10, 6), dpi=300)
ax.set_xlim(0, 10)
ax.set_ylim(0, 7)
ax.axis('off')

# Title
ax.text(5, 6.6, "System Architecture - 3-Tier Layered Design", ha='center', va='center', fontsize=14, fontweight='bold', color='#1A365D')

# Presentation Layer
p_rect = patches.FancyBboxPatch((1, 4.8), 8, 1.2, boxstyle="round,pad=0.1", ec="#2B6CB0", fc="#EBF8FF", lw=2)
ax.add_patch(p_rect)
ax.text(5, 5.6, "Presentation Layer (UI)", ha='center', va='center', fontsize=12, fontweight='bold', color='#2B6CB0')
ax.text(5, 5.15, "LibraryApp.java (Console Menus, Input Validation, Table Rendering)", ha='center', va='center', fontsize=10, color='#2D3748')

# Service Layer
s_rect = patches.FancyBboxPatch((1, 2.8), 8, 1.3, boxstyle="round,pad=0.1", ec="#2F855A", fc="#F0FFF4", lw=2)
ax.add_patch(s_rect)
ax.text(5, 3.75, "Service Layer (Business Logic & Domain)", ha='center', va='center', fontsize=12, fontweight='bold', color='#2F855A')
ax.text(5, 3.3, "LibraryService.java (Circulation, Fines, Analytics, Search)", ha='center', va='center', fontsize=10, color='#2D3748')
ax.text(5, 2.95, "Domain Models: Book | Member | Transaction | Custom Exceptions", ha='center', va='center', fontsize=9, fontstyle='italic', color='#4A5568')

# Persistence Layer
d_rect = patches.FancyBboxPatch((1, 0.8), 8, 1.3, boxstyle="round,pad=0.1", ec="#C53030", fc="#FFF5F5", lw=2)
ax.add_patch(d_rect)
ax.text(5, 1.75, "Data Persistence & Storage Layer", ha='center', va='center', fontsize=12, fontweight='bold', color='#C53030')
ax.text(5, 1.3, "FileStorageService.java (CSV Parsing, Serialization, Data Seeding)", ha='center', va='center', fontsize=10, color='#2D3748')
ax.text(5, 0.95, "Flat Files: data/books.csv | data/members.csv | data/transactions.csv", ha='center', va='center', fontsize=9, fontstyle='italic', color='#4A5568')

# Arrows
ax.annotate('', xy=(5, 4.8), xytext=(5, 4.1), arrowprops=dict(arrowstyle="<->", lw=2, color='#4A5568'))
ax.annotate('', xy=(5, 2.8), xytext=(5, 2.1), arrowprops=dict(arrowstyle="<->", lw=2, color='#4A5568'))

plt.tight_layout()
plt.savefig("docs/images/architecture_diagram.png", bbox_inches='tight')
plt.close()

# 2. Workflow Diagram
fig, ax = plt.subplots(figsize=(11, 4.5), dpi=300)
ax.set_xlim(0, 11)
ax.set_ylim(0, 4)
ax.axis('off')

ax.text(5.5, 3.6, "Process Flow: Book Borrowing & Return Lifecycle", ha='center', va='center', fontsize=13, fontweight='bold', color='#1A365D')

nodes = [
    ("User\nRequest", 1.0, 2.0, "#ED8936", "#FFFAF0"),
    ("Validate\nPatron & Book", 3.0, 2.0, "#3182CE", "#EBF8FF"),
    ("Check Limits\n& Availability", 5.0, 2.0, "#805AD5", "#FAF5FF"),
    ("Create Tx &\nDecrement Qty", 7.0, 2.0, "#38A169", "#F0FFF4"),
    ("Return Book &\nCalculate Fine", 9.5, 2.0, "#E53E3E", "#FFF5F5")
]

for label, x, y, ec, fc in nodes:
    box = patches.FancyBboxPatch((x-0.8, y-0.6), 1.6, 1.2, boxstyle="round,pad=0.1", ec=ec, fc=fc, lw=1.8)
    ax.add_patch(box)
    ax.text(x, y, label, ha='center', va='center', fontsize=9, fontweight='bold', color='#1A202C')

for i in range(len(nodes)-1):
    x1 = nodes[i][1] + 0.85
    x2 = nodes[i+1][1] - 0.85
    ax.annotate('', xy=(x2, 2.0), xytext=(x1, 2.0), arrowprops=dict(arrowstyle="-|>", lw=2, color='#4A5568'))

plt.tight_layout()
plt.savefig("docs/images/workflow_diagram.png", bbox_inches='tight')
plt.close()

# 3. Class Diagram
fig, ax = plt.subplots(figsize=(12, 7.5), dpi=300)
ax.set_xlim(0, 12)
ax.set_ylim(0, 8)
ax.axis('off')

ax.text(6, 7.6, "UML Class Diagram - Core Domain Entities", ha='center', va='center', fontsize=14, fontweight='bold', color='#1A365D')

def draw_class(x, y, w, h, title, fields, methods, color="#2B6CB0"):
    box = patches.Rectangle((x, y), w, h, ec=color, fc="white", lw=1.5)
    ax.add_patch(box)
    head = patches.Rectangle((x, y + h - 0.6), w, 0.6, ec=color, fc=color, lw=1.5)
    ax.add_patch(head)
    ax.text(x + w/2, y + h - 0.3, title, ha='center', va='center', color='white', fontweight='bold', fontsize=10)
    
    # Fields
    fy = y + h - 0.8
    for f in fields:
        ax.text(x + 0.15, fy, "- " + f, va='top', fontsize=8, color="#2D3748")
        fy -= 0.28
    
    # Divider
    ax.plot([x, x + w], [fy, fy], color=color, lw=1)
    fy -= 0.15
    # Methods
    for m in methods:
        ax.text(x + 0.15, fy, "+ " + m, va='top', fontsize=8, color="#2D3748")
        fy -= 0.28

draw_class(0.5, 3.2, 3.4, 3.8, "Book", 
           ["bookId: String", "title: String", "author: String", "genre: String", "totalCopies: int", "availableCopies: int"],
           ["isAvailable(): boolean", "borrowCopy(): void", "returnCopy(): void", "toCsv(): String", "fromCsv(str): Book"])

draw_class(4.3, 3.2, 3.4, 3.8, "Member", 
           ["memberId: String", "name: String", "email: String", "phone: String", "activeBorrowCount: int", "MAX_BORROW_LIMIT: int"],
           ["canBorrow(): boolean", "incrementBorrowCount()", "decrementBorrowCount()", "toCsv(): String", "fromCsv(str): Member"])

draw_class(8.1, 3.0, 3.5, 4.0, "Transaction", 
           ["transactionId: String", "bookId: String", "memberId: String", "borrowDate: LocalDate", "dueDate: LocalDate", "returnDate: LocalDate", "status: Status"],
           ["markReturned(): void", "refreshStatus(): void", "isOverdue(): boolean", "calculateFine(): double", "toCsv(): String"])

# LibraryService at bottom
draw_class(2.5, 0.3, 7.0, 2.3, "LibraryService",
           ["bookCatalog: Map<String,Book>", "memberRegistry: Map<String,Member>", "transactionLog: Map<String,Transaction>"],
           ["borrowBook(bId, mId): Transaction", "returnBook(txId): Transaction", "searchBooks(q): List<Book>", "getSystemSummary(): Map"])

# Connectors
ax.annotate('', xy=(2.2, 3.2), xytext=(4.0, 2.6), arrowprops=dict(arrowstyle="<|-", lw=1.5, color='#4A5568'))
ax.annotate('', xy=(6.0, 3.2), xytext=(6.0, 2.6), arrowprops=dict(arrowstyle="<|-", lw=1.5, color='#4A5568'))
ax.annotate('', xy=(9.8, 3.0), xytext=(8.0, 2.6), arrowprops=dict(arrowstyle="<|-", lw=1.5, color='#4A5568'))

plt.tight_layout()
plt.savefig("docs/images/class_diagram.png", bbox_inches='tight')
plt.close()

# 4. Storage / ER Diagram
fig, ax = plt.subplots(figsize=(10, 4.5), dpi=300)
ax.set_xlim(0, 10)
ax.set_ylim(0, 5)
ax.axis('off')

ax.text(5, 4.6, "Entity Relationship (ER) & Flat-File Storage Schema", ha='center', va='center', fontsize=13, fontweight='bold', color='#1A365D')

def draw_er_entity(x, y, w, h, name, attrs):
    box = patches.Rectangle((x, y), w, h, ec="#2B6CB0", fc="#F7FAFC", lw=1.5)
    ax.add_patch(box)
    head = patches.Rectangle((x, y + h - 0.5), w, 0.5, ec="#2B6CB0", fc="#2B6CB0")
    ax.add_patch(head)
    ax.text(x + w/2, y + h - 0.25, name, ha='center', va='center', color='white', fontweight='bold', fontsize=10)
    ay = y + h - 0.7
    for a in attrs:
        ax.text(x + 0.15, ay, a, va='top', fontsize=8, color="#2D3748")
        ay -= 0.28

draw_er_entity(0.5, 1.2, 2.6, 2.8, "BOOK (CSV)", ["PK: bookId", "title", "author", "genre", "totalCopies", "availableCopies"])
draw_er_entity(6.9, 1.2, 2.6, 2.8, "MEMBER (CSV)", ["PK: memberId", "name", "email", "phone", "activeBorrowCount"])
draw_er_entity(3.6, 0.5, 2.8, 3.5, "TRANSACTION (CSV)", ["PK: transactionId", "FK: bookId", "FK: memberId", "borrowDate", "dueDate", "returnDate", "status"])

# Lines
ax.annotate('', xy=(3.6, 2.5), xytext=(3.1, 2.5), arrowprops=dict(arrowstyle="<|-|>", lw=1.5, color='#4A5568'))
ax.text(3.35, 2.7, "1:N", ha='center', fontsize=9, fontweight='bold')
ax.annotate('', xy=(6.4, 2.5), xytext=(6.9, 2.5), arrowprops=dict(arrowstyle="<|-|>", lw=1.5, color='#4A5568'))
ax.text(6.65, 2.7, "1:N", ha='center', fontsize=9, fontweight='bold')

plt.tight_layout()
plt.savefig("docs/images/er_diagram.png", bbox_inches='tight')
plt.close()

print("All diagrams successfully generated!")
