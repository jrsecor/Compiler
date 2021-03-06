.data
.comm	a,4,4

.text
	.align 4
.globl  addThem
addThem:
addThem_bb2:
	movl	%EDI, %EAX
	movl	%ESI, %EDI
addThem_bb3:
	addl	%EDI, %EAX
addThem_bb1:
	ret
.globl  putDigit
putDigit:
putDigit_bb2:
putDigit_bb3:
	movl	$48, %EAX
	addl	%EDI, %EAX
	movl	%EAX, %EDI
	call	putchar
putDigit_bb1:
	ret
.globl  printInt
printInt:
printInt_bb2:
	pushq	%R14
	pushq	%R15
	movl	%EDI, %R14D
printInt_bb3:
	movl	$0, %R15D
	cmpl	$10000, %R14D
	jl	printInt_bb6
printInt_bb4:
	movl	$45, %EDI
	call	putchar
	movl	$1, %EDI
	call	putDigit
printInt_bb1:
	popq	%R15
	popq	%R14
	ret
printInt_bb11:
	cmpl	$1, %R15D
	jne	printInt_bb10
printInt_bb12:
	movl	$0, %EDI
	call	putDigit
printInt_bb13:
	jmp	printInt_bb10
printInt_bb16:
	cmpl	$1, %R15D
	jne	printInt_bb15
printInt_bb17:
	movl	$0, %EDI
	call	putDigit
printInt_bb18:
	jmp	printInt_bb15
printInt_bb6:
	cmpl	$1000, %R14D
	jl	printInt_bb8
printInt_bb7:
	movl	$0, %EDX
	movl	%R14D, %EAX
	movl	$1000, %EDI
	idivl	%EDI, %EAX
	movl	%EAX, %R15D
	movl	%R15D, %EDI
	call	putDigit
	movl	%R15D, %EAX
	movl	$1000, %EDI
	imull	%EDI, %EAX
	movl	%EAX, %EDI
	movl	%R14D, %EAX
	subl	%EDI, %EAX
	movl	%EAX, %R14D
	movl	$1, %R15D
printInt_bb8:
	cmpl	$100, %R14D
	jl	printInt_bb11
printInt_bb9:
	movl	$0, %EDX
	movl	%R14D, %EAX
	movl	$100, %EDI
	idivl	%EDI, %EAX
	movl	%EAX, %R15D
	movl	%R15D, %EDI
	call	putDigit
	movl	%R15D, %EAX
	movl	$100, %EDI
	imull	%EDI, %EAX
	movl	%EAX, %EDI
	movl	%R14D, %EAX
	subl	%EDI, %EAX
	movl	%EAX, %R14D
	movl	$1, %R15D
printInt_bb10:
	cmpl	$10, %R14D
	jl	printInt_bb16
printInt_bb14:
	movl	$0, %EDX
	movl	%R14D, %EAX
	movl	$10, %EDI
	idivl	%EDI, %EAX
	movl	%EAX, %R15D
	movl	%R15D, %EDI
	call	putDigit
	movl	%R15D, %EAX
	movl	$10, %EDI
	imull	%EDI, %EAX
	movl	%EAX, %EDI
	movl	%R14D, %EAX
	subl	%EDI, %EAX
	movl	%EAX, %R14D
printInt_bb15:
	movl	%R14D, %EDI
	call	putDigit
	jmp	printInt_bb1
.globl  main
main:
main_bb2:
	pushq	%R14
	pushq	%R15
main_bb3:
	movl	$5, %R15D
	movl	%R15D, %ESI
	cmpl	$5, %ESI
	jne	main_bb6
main_bb4:
	movl	$3, a(%RIP)
main_bb5:
	movl	$0, %R14D
	movl	$1, %R15D
	cmpl	$8, %R15D
	jg	main_bb8
main_bb7:
	movl	%R14D, %EAX
	addl	%R15D, %EAX
	movl	%EAX, %R14D
	movl	%R15D, %EAX
	addl	$1, %EAX
	movl	%EAX, %R15D
	cmpl	$8, %R15D
	jle	main_bb7
main_bb8:
	movl	$0, %EDX
	movl	%R14D, %EAX
	movl	$3, %EDI
	idivl	%EDI, %EAX
	movl	$4, %EDI
	imull	%EDI, %EAX
	movl	%EAX, %R14D
	movl	a(%RIP), %EAX
	movl	%EAX, %EDI
	call	addThem
	movl	%EAX, %R15D
	movl	$56, %EDI
	call	putchar
	movl	$61, %EDI
	call	putchar
	movl	%R15D, %EAX
	addl	%R14D, %EAX
	movl	%EAX, %EDI
	call	putchar
	movl	$10, %EDI
	call	putchar
	movl	$0, %R15D
	cmpl	$10, %R15D
	jge	main_bb10
main_bb9:
	movl	$48, %EAX
	addl	%R15D, %EAX
	movl	%EAX, %EDI
	call	putchar
	movl	%R15D, %EAX
	addl	$1, %EAX
	movl	%EAX, %R15D
	cmpl	$10, %R15D
	jl	main_bb9
main_bb10:
	movl	$10, %EDI
	call	putchar
	movl	$67, %EDI
	call	putchar
	movl	$83, %EDI
	call	putchar
	movl	$3510, %EDI
	call	printInt
	movl	$10, %EDI
	call	putchar
	movl	$0, %ESI
	movl	$1, %R15D
	movl	$1, %R14D
	movl	$0, %EAX
	cmpl	$0, %ESI
	jne	main_bb13
main_bb11:
	cmpl	$0, %R15D
	jne	main_bb16
main_bb14:
	movl	$1, %R15D
main_bb12:
	cmpl	$10, %R15D
	jne	main_bb25
main_bb23:
	movl	$99, %EDI
	call	putchar
	movl	$0, %EDI
	call	putDigit
	movl	$0, %EDI
	call	putDigit
	movl	$108, %EDI
	call	putchar
main_bb24:
	movl	$10, %EDI
	call	putchar
	movl	$0, %EAX
main_bb1:
	popq	%R15
	popq	%R14
	ret
main_bb6:
	movl	$4, a(%RIP)
	jmp	main_bb5
main_bb22:
	movl	$3, %R15D
	jmp	main_bb12
main_bb19:
	cmpl	$0, %EAX
	jne	main_bb22
main_bb20:
	movl	$10, %R15D
main_bb21:
	jmp	main_bb12
main_bb16:
	cmpl	$0, %R14D
	jne	main_bb19
main_bb17:
	movl	$2, %R15D
main_bb18:
	jmp	main_bb12
main_bb13:
	movl	$0, %R15D
	jmp	main_bb12
main_bb25:
	movl	$98, %EDI
	call	putchar
	movl	$97, %EDI
	call	putchar
	movl	$100, %EDI
	call	putchar
	movl	$61, %EDI
	call	putchar
	movl	%R15D, %EDI
	call	printInt
	jmp	main_bb24
