#include "uart4.h"

//基本功能都能实现，可以再完善一下
//串口4采用数包的方式接收数据或时间间隔的方式
//时间间隔的方法明天补充！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

unsigned char Com4RecArr[C4RAS] = {0};//接收串口数据的数组，请按实际需要修改大小
unsigned char Com4RecComplete = 0;//数据接收成功的标志位，接收完成后需手动置零

//初始化串口4
//串口4的速度是串口1的一半，使用时需注意
//pclk1:PCLK1时钟频率(MHz)
//bound:波特率 
//串口4的TX在PC10，RX在PC11
//串口4没用使用DMA，发送数据效率会比较低，建议连接数据量较小的外设
void UART4_Init(unsigned int pclk1, unsigned int bound)
{
	float temp;
	unsigned short int mantissa;
	unsigned short int fraction;
	
	RCC->APB2ENR |= 0x00000010;//使能PORTC时钟
	GPIOC->CRH &= 0xFFFF00FF;//设置IO口功能复用
	GPIOC->CRH |= 0x00008B00;

	RCC->APB1ENR |= 0x00080000;//使能串口4时钟
	RCC->APB1RSTR |= 0x00080000;//复位串口4
	RCC->APB1RSTR &= 0xFFF7FFFF;//停止复位串口4
	
	//USARTDIV中的值=主频/波特率/16
	temp = (float)(pclk1*1000000)/(bound*16);//得到USARTDIV
	mantissa = temp;//得到整数部分
	fraction = (temp-mantissa)*16;//得到小数部分
	mantissa <<= 4;//整数部分左移四位
	mantissa += fraction;//把计算出的小数部分补到低四位
	    	
	//USART3->BRR = (pclk1*1000000)/(bound);//这样设置的波特率精较低
	UART4->BRR = mantissa;//这样设置的波特率精度更高
	
	UART4->CR1 |= 0x0000200C;//8位数据，无校验位，启用收发使能
	//CR2、CR3采用默认设置，

	//开启接收中断使能
	UART4->CR1 |= 0x00000100;//PE中断使能
	UART4->CR1 |= 0x00000020;//接收缓冲区非空中断使能
	MY_NVIC_Init(3, 3, UART4_IRQn, 2);//抢占3，子优先级3，组2。这样设置才应该是最低优先级吧？！
}

//串口4中断服务
//寄存器的详细设定参见《STM32中文参考手册》540页
void UART4_IRQHandler(void)//串口中断服务，有校验
{
	static unsigned char Com4RecCount = 0;//数据接收计数器
	static unsigned char Com4CheckSum = 0;//求和校验

	//如果没有收到数据，直接返回
	if(!(UART4->SR & 0x00000020))
		return;

	Com4RecArr[Com4RecCount] = UART4->DR;//将接收的数据存入数组
	Com4CheckSum += Com4RecArr[Com4RecCount];//求和校验

	//这部分代码为数据接收
	if(Com4RecCount == 0 && Com4RecArr[Com4RecCount] == COM4HEAD1)//判断帧头
	{
		Com4RecCount = 1;
	}
	else if(Com4RecCount == 1 && Com4RecArr[Com4RecCount] == COM4HEAD2)//判断帧头
	{
		Com4RecCount = 2;
	}
	else if(Com4RecCount >= 2 && Com4RecCount < C4RAS - 1)//数据部分，长度按情况修改，
	{
		Com4RecCount++;
	}

	//有校验位
	else if(Com4RecCount == C4RAS - 1 && Com4RecArr[Com4RecCount] == Com4CheckSum - Com4RecArr[Com4RecCount])//我是在前面先求和的，所以这里要减
	{
		Com4RecComplete = 1;//接收成功，标志位置1
		Com4CheckSum = 0;//校验和置零，否则数据要接收两遍才能将ComRecComplete置一
		Com4RecCount = 0;//计数器置零
	}
	
	else
	{
		//ComRecError = 1;
		Com4RecCount = 0;//如果条件都不满足，就将计数器清零，前面接收的数据作废
		Com4CheckSum = 0;
	}
}
