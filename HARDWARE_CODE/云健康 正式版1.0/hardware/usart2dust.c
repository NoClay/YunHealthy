#include "usart2dust.h"

//#define RAS 10//receive array size,接收串口数据的数组大小，请按实际需要修改大小，含求和校验位
//#define RECHEAD1 0xAA//接收数据的帧头
//#define RECHEAD2 0xBB//接收数据的帧头

unsigned char Com2RecArr[RAS] = {0};//接收串口数据的数组，请按实际需要修改大小

//这几位是不是也学他的编写方式？
//bit ComFlag = 0;//接收数据标志位，也可表示产生了数据收发
unsigned char Com2RecComplete = 0;//数据接收成功的标志位，接收完成后需手动置零
//bit ComRecError = 0;//数据接收错误，手动置零

//初始化串口3
//pclk1:PCLK1时钟频率(Mhz)
//bound:波特率 
//串口3的TX在PB10，RX在PB11
//串口3没用使用DMA，发送数据效率会比较低，建议连接数据量较小的外设
void USART2_Init(unsigned int pclk1, unsigned int bound)
{
	float temp;
	unsigned short int mantissa;
	unsigned short int fraction;
	
	RCC->APB2ENR |= 0x00000004;//使能PORTA时钟
	GPIOA->CRL &= 0xFFFF00FF;
	GPIOA->CRL |= 0x00008B00;

	RCC->APB1ENR |= 0x00020000;//使能串口2时钟
	RCC->APB1RSTR |= 0x00020000;//复位串口2
	RCC->APB1RSTR &= 0xFFFDFFFF;//停止复位串口2
	
	//USARTDIV中的值=主频/波特率/16
	temp = (float)(pclk1*1000000)/(bound*16);//得到USARTDIV
	mantissa = temp;//得到整数部分
	fraction = (temp-mantissa)*16;//得到小数部分
	mantissa <<= 4;//整数部分左移四位
	mantissa += fraction;//把计算出的小数部分补到低四位
	    	
	//USART3->BRR = (pclk1*1000000)/(bound);//这样设置的波特率精较低
	USART2->BRR = mantissa;//这样设置的波特率精度更高
	
	USART2->CR1 |= 0x0000200C;//8位数据，无校验位，启用收发使能
	//CR2、CR3采用默认设置，
	
	//发送部分完成了，剩下的我就不知咋写了。
	//他的接收使能是咋回事还没看懂
	//使能接收中断
	
	//要这个必要吗？一定要默认定义接收使能？干脆默认打开拉倒。
	//#ifdef USART2_RX_EN//如果使能了接收
	//USART3->CR1 |= 1<<8;//PE中断使能
	//USART3->CR1 |= 1<<5;//接收缓冲区非空中断使能
	USART2->CR1 |= 0x00000100;//PE中断使能
	USART2->CR1 |= 0x00000020;//接收缓冲区非空中断使能
	MY_NVIC_Init(2, 3, USART2_IRQn, 2);//抢占2，子优先级3，组2。这样设置才应该是最低优先级吧？！
	
/*
//要这个必要吗？一定要默认定义接收使能？干脆默认打开拉倒。
#ifdef USART2_RX_EN		  	//如果使能了接收
	//使能接收中断
	USART2->CR1|=1<<8;    	//PE中断使能
	USART2->CR1|=1<<5;    	//接收缓冲区非空中断使能
	MY_NVIC_Init(2,3,USART2_IRQn,2);//组2，最低优先级
	TIM4_Init(99,7199);		//10ms中断
	USART2_RX_STA=0;		//清零
	TIM4_Set(0);			//关闭定时器4
#endif
*/
}

//串口3中断服务
//寄存器的详细设定参见《STM32中文参考手册》540页
void USART2_IRQHandler(void)//串口中断服务，有校验
{
	static unsigned char Com2RecCount = 0;//数据接收计数器
	static unsigned char Com2CheckSum = 0;//求和校验

	//如果没有收到数据，直接返回
	if(!(USART2->SR & 0x00000020))
		return;
	
	Com2RecArr[Com2RecCount] = USART2->DR;//将接收的数据存入数组
	if(Com2RecCount == 0 && Com2RecArr[Com2RecCount] == RECHEAD1)//判断帧头
	{
		Com2RecCount = 1;
	}
	else if(Com2RecCount >= 1 && Com2RecCount < RAS - 1)//数据部分，长度按情况修改，
	{
		Com2RecCount++;
	}
	else if(Com2RecCount == RAS - 1 && Com2RecArr[Com2RecCount] == 0xFF)
	{
		Com2RecComplete = 1;//接收成功，标志位置1
		//Com3CheckSum = 0;//校验和置零，否则数据要接收两遍才能将ComRecComplete置一
		Com2RecCount = 0;//计数器置零
		//ComRecError = 0;//接收无误
		//ES = 0;//这句还不确定
	}
	else
	{
		//ComRecError = 1;
		Com2RecCount = 0;//如果条件都不满足，就将计数器清零，前面接收的数据作废
		Com2CheckSum = 0;
	}
/*
	Com3RecArr[Com3RecCount] = USART3->DR;//将接收的数据存入数组
	Com3CheckSum += Com3RecArr[Com3RecCount];//求和校验

	//这部分代码为数据接收
	if(Com3RecCount == 0 && Com3RecArr[Com3RecCount] == RECHEAD1)//判断帧头
	{
		Com3RecCount = 1;
	}
//	else if(Com3RecCount == 1 && Com3RecArr[Com3RecCount] == RECHEAD2)//判断帧头
//	{
//		Com3RecCount = 2;
//	}
	else if(Com3RecCount >= 1 && Com3RecCount < RAS - 1)//数据部分，长度按情况修改，
	{
		Com3RecCount++;
	}

	//有校验位
	//我是在前面先求和的，所以这里要减，pm2.5发送的校验数据不含帧头，所以还要再减0xAA
	else if(Com3RecCount == RAS - 1 && Com3RecArr[Com3RecCount] == Com3CheckSum - Com3RecArr[Com3RecCount] - 0xAA)
	{
		Com3RecComplete = 1;//接收成功，标志位置1
		Com3CheckSum = 0;//校验和置零，否则数据要接收两遍才能将ComRecComplete置一
		Com3RecCount = 0;//计数器置零
		//ComRecError = 0;//接收无误
		//ES = 0;//这句还不确定
	}
	
	else
	{
		//ComRecError = 1;
		Com3RecCount = 0;//如果条件都不满足，就将计数器清零，前面接收的数据作废
		Com3CheckSum = 0;
	}

	//ComFlag = 1;
*/	
}
