#include "uart4.h"

//�������ܶ���ʵ�֣�����������һ��
//����4���������ķ�ʽ�������ݻ�ʱ�����ķ�ʽ
//ʱ�����ķ������첹�䣡����������������������������������������������������������������������������������

unsigned char Com4RecArr[C4RAS] = {0};//���մ������ݵ����飬�밴ʵ����Ҫ�޸Ĵ�С
unsigned char Com4RecComplete = 0;//���ݽ��ճɹ��ı�־λ��������ɺ����ֶ�����

//��ʼ������4
//����4���ٶ��Ǵ���1��һ�룬ʹ��ʱ��ע��
//pclk1:PCLK1ʱ��Ƶ��(MHz)
//bound:������ 
//����4��TX��PC10��RX��PC11
//����4û��ʹ��DMA����������Ч�ʻ�Ƚϵͣ�����������������С������
void UART4_Init(unsigned int pclk1, unsigned int bound)
{
	float temp;
	unsigned short int mantissa;
	unsigned short int fraction;
	
	RCC->APB2ENR |= 0x00000010;//ʹ��PORTCʱ��
	GPIOC->CRH &= 0xFFFF00FF;//����IO�ڹ��ܸ���
	GPIOC->CRH |= 0x00008B00;

	RCC->APB1ENR |= 0x00080000;//ʹ�ܴ���4ʱ��
	RCC->APB1RSTR |= 0x00080000;//��λ����4
	RCC->APB1RSTR &= 0xFFF7FFFF;//ֹͣ��λ����4
	
	//USARTDIV�е�ֵ=��Ƶ/������/16
	temp = (float)(pclk1*1000000)/(bound*16);//�õ�USARTDIV
	mantissa = temp;//�õ���������
	fraction = (temp-mantissa)*16;//�õ�С������
	mantissa <<= 4;//��������������λ
	mantissa += fraction;//�Ѽ������С�����ֲ�������λ
	    	
	//USART3->BRR = (pclk1*1000000)/(bound);//�������õĲ����ʾ��ϵ�
	UART4->BRR = mantissa;//�������õĲ����ʾ��ȸ���
	
	UART4->CR1 |= 0x0000200C;//8λ���ݣ���У��λ�������շ�ʹ��
	//CR2��CR3����Ĭ�����ã�

	//���������ж�ʹ��
	UART4->CR1 |= 0x00000100;//PE�ж�ʹ��
	UART4->CR1 |= 0x00000020;//���ջ������ǿ��ж�ʹ��
	MY_NVIC_Init(3, 3, UART4_IRQn, 2);//��ռ3�������ȼ�3����2���������ò�Ӧ����������ȼ��ɣ���
}

//����4�жϷ���
//�Ĵ�������ϸ�趨�μ���STM32���Ĳο��ֲᡷ540ҳ
void UART4_IRQHandler(void)//�����жϷ�����У��
{
	static unsigned char Com4RecCount = 0;//���ݽ��ռ�����
	static unsigned char Com4CheckSum = 0;//���У��

	//���û���յ����ݣ�ֱ�ӷ���
	if(!(UART4->SR & 0x00000020))
		return;

	Com4RecArr[Com4RecCount] = UART4->DR;//�����յ����ݴ�������
	Com4CheckSum += Com4RecArr[Com4RecCount];//���У��

	//�ⲿ�ִ���Ϊ���ݽ���
	if(Com4RecCount == 0 && Com4RecArr[Com4RecCount] == COM4HEAD1)//�ж�֡ͷ
	{
		Com4RecCount = 1;
	}
	else if(Com4RecCount == 1 && Com4RecArr[Com4RecCount] == COM4HEAD2)//�ж�֡ͷ
	{
		Com4RecCount = 2;
	}
	else if(Com4RecCount >= 2 && Com4RecCount < C4RAS - 1)//���ݲ��֣����Ȱ�����޸ģ�
	{
		Com4RecCount++;
	}

	//��У��λ
	else if(Com4RecCount == C4RAS - 1 && Com4RecArr[Com4RecCount] == Com4CheckSum - Com4RecArr[Com4RecCount])//������ǰ������͵ģ���������Ҫ��
	{
		Com4RecComplete = 1;//���ճɹ�����־λ��1
		Com4CheckSum = 0;//У������㣬��������Ҫ����������ܽ�ComRecComplete��һ
		Com4RecCount = 0;//����������
	}
	
	else
	{
		//ComRecError = 1;
		Com4RecCount = 0;//��������������㣬�ͽ����������㣬ǰ����յ���������
		Com4CheckSum = 0;
	}
}
