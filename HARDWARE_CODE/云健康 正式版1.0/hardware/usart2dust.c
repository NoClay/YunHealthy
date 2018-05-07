#include "usart2dust.h"

//#define RAS 10//receive array size,���մ������ݵ������С���밴ʵ����Ҫ�޸Ĵ�С�������У��λ
//#define RECHEAD1 0xAA//�������ݵ�֡ͷ
//#define RECHEAD2 0xBB//�������ݵ�֡ͷ

unsigned char Com2RecArr[RAS] = {0};//���մ������ݵ����飬�밴ʵ����Ҫ�޸Ĵ�С

//�⼸λ�ǲ���Ҳѧ���ı�д��ʽ��
//bit ComFlag = 0;//�������ݱ�־λ��Ҳ�ɱ�ʾ�����������շ�
unsigned char Com2RecComplete = 0;//���ݽ��ճɹ��ı�־λ��������ɺ����ֶ�����
//bit ComRecError = 0;//���ݽ��մ����ֶ�����

//��ʼ������3
//pclk1:PCLK1ʱ��Ƶ��(Mhz)
//bound:������ 
//����3��TX��PB10��RX��PB11
//����3û��ʹ��DMA����������Ч�ʻ�Ƚϵͣ�����������������С������
void USART2_Init(unsigned int pclk1, unsigned int bound)
{
	float temp;
	unsigned short int mantissa;
	unsigned short int fraction;
	
	RCC->APB2ENR |= 0x00000004;//ʹ��PORTAʱ��
	GPIOA->CRL &= 0xFFFF00FF;
	GPIOA->CRL |= 0x00008B00;

	RCC->APB1ENR |= 0x00020000;//ʹ�ܴ���2ʱ��
	RCC->APB1RSTR |= 0x00020000;//��λ����2
	RCC->APB1RSTR &= 0xFFFDFFFF;//ֹͣ��λ����2
	
	//USARTDIV�е�ֵ=��Ƶ/������/16
	temp = (float)(pclk1*1000000)/(bound*16);//�õ�USARTDIV
	mantissa = temp;//�õ���������
	fraction = (temp-mantissa)*16;//�õ�С������
	mantissa <<= 4;//��������������λ
	mantissa += fraction;//�Ѽ������С�����ֲ�������λ
	    	
	//USART3->BRR = (pclk1*1000000)/(bound);//�������õĲ����ʾ��ϵ�
	USART2->BRR = mantissa;//�������õĲ����ʾ��ȸ���
	
	USART2->CR1 |= 0x0000200C;//8λ���ݣ���У��λ�������շ�ʹ��
	//CR2��CR3����Ĭ�����ã�
	
	//���Ͳ�������ˣ�ʣ�µ��ҾͲ�֪զд�ˡ�
	//���Ľ���ʹ����զ���»�û����
	//ʹ�ܽ����ж�
	
	//Ҫ�����Ҫ��һ��ҪĬ�϶������ʹ�ܣ��ɴ�Ĭ�ϴ�������
	//#ifdef USART2_RX_EN//���ʹ���˽���
	//USART3->CR1 |= 1<<8;//PE�ж�ʹ��
	//USART3->CR1 |= 1<<5;//���ջ������ǿ��ж�ʹ��
	USART2->CR1 |= 0x00000100;//PE�ж�ʹ��
	USART2->CR1 |= 0x00000020;//���ջ������ǿ��ж�ʹ��
	MY_NVIC_Init(2, 3, USART2_IRQn, 2);//��ռ2�������ȼ�3����2���������ò�Ӧ����������ȼ��ɣ���
	
/*
//Ҫ�����Ҫ��һ��ҪĬ�϶������ʹ�ܣ��ɴ�Ĭ�ϴ�������
#ifdef USART2_RX_EN		  	//���ʹ���˽���
	//ʹ�ܽ����ж�
	USART2->CR1|=1<<8;    	//PE�ж�ʹ��
	USART2->CR1|=1<<5;    	//���ջ������ǿ��ж�ʹ��
	MY_NVIC_Init(2,3,USART2_IRQn,2);//��2��������ȼ�
	TIM4_Init(99,7199);		//10ms�ж�
	USART2_RX_STA=0;		//����
	TIM4_Set(0);			//�رն�ʱ��4
#endif
*/
}

//����3�жϷ���
//�Ĵ�������ϸ�趨�μ���STM32���Ĳο��ֲᡷ540ҳ
void USART2_IRQHandler(void)//�����жϷ�����У��
{
	static unsigned char Com2RecCount = 0;//���ݽ��ռ�����
	static unsigned char Com2CheckSum = 0;//���У��

	//���û���յ����ݣ�ֱ�ӷ���
	if(!(USART2->SR & 0x00000020))
		return;
	
	Com2RecArr[Com2RecCount] = USART2->DR;//�����յ����ݴ�������
	if(Com2RecCount == 0 && Com2RecArr[Com2RecCount] == RECHEAD1)//�ж�֡ͷ
	{
		Com2RecCount = 1;
	}
	else if(Com2RecCount >= 1 && Com2RecCount < RAS - 1)//���ݲ��֣����Ȱ�����޸ģ�
	{
		Com2RecCount++;
	}
	else if(Com2RecCount == RAS - 1 && Com2RecArr[Com2RecCount] == 0xFF)
	{
		Com2RecComplete = 1;//���ճɹ�����־λ��1
		//Com3CheckSum = 0;//У������㣬��������Ҫ����������ܽ�ComRecComplete��һ
		Com2RecCount = 0;//����������
		//ComRecError = 0;//��������
		//ES = 0;//��仹��ȷ��
	}
	else
	{
		//ComRecError = 1;
		Com2RecCount = 0;//��������������㣬�ͽ����������㣬ǰ����յ���������
		Com2CheckSum = 0;
	}
/*
	Com3RecArr[Com3RecCount] = USART3->DR;//�����յ����ݴ�������
	Com3CheckSum += Com3RecArr[Com3RecCount];//���У��

	//�ⲿ�ִ���Ϊ���ݽ���
	if(Com3RecCount == 0 && Com3RecArr[Com3RecCount] == RECHEAD1)//�ж�֡ͷ
	{
		Com3RecCount = 1;
	}
//	else if(Com3RecCount == 1 && Com3RecArr[Com3RecCount] == RECHEAD2)//�ж�֡ͷ
//	{
//		Com3RecCount = 2;
//	}
	else if(Com3RecCount >= 1 && Com3RecCount < RAS - 1)//���ݲ��֣����Ȱ�����޸ģ�
	{
		Com3RecCount++;
	}

	//��У��λ
	//������ǰ������͵ģ���������Ҫ����pm2.5���͵�У�����ݲ���֡ͷ�����Ի�Ҫ�ټ�0xAA
	else if(Com3RecCount == RAS - 1 && Com3RecArr[Com3RecCount] == Com3CheckSum - Com3RecArr[Com3RecCount] - 0xAA)
	{
		Com3RecComplete = 1;//���ճɹ�����־λ��1
		Com3CheckSum = 0;//У������㣬��������Ҫ����������ܽ�ComRecComplete��һ
		Com3RecCount = 0;//����������
		//ComRecError = 0;//��������
		//ES = 0;//��仹��ȷ��
	}
	
	else
	{
		//ComRecError = 1;
		Com3RecCount = 0;//��������������㣬�ͽ����������㣬ǰ����յ���������
		Com3CheckSum = 0;
	}

	//ComFlag = 1;
*/	
}
