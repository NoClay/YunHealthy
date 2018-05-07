#include "usart2.h"
#include "math.h"

char LongtitudeDirection = 'E', LatitudeDirection = 'N';
double longtitude=0.0, latitude=0.0;

unsigned char getgps(char *p)
{
	unsigned short int i=0, j=1, length;//j���ڼ�¼�Ѿ��ҵ��Ķ��ŵĸ���
	unsigned char e;//�˷������ָ��
	unsigned short int comma[13] = {0};//���ڱ��涺�ŵ�λ�ã�Ϊ�˷��㴦������comma[0]
	int coordinate;//int��һ��ͱ��治��������Ϣ�ˣ����ա���18 0000 0000

	length = USART2_RX_STA & 0x7FFF;
	USART2_RX_STA = 0;
	
	//�������2�ĵ�1���ַ�����$��˵���յ��Ĳ���GPS��Ϣ��ֱ�ӷ���0
	//�������2�ĵ�4���ַ�����R��˵���յ��Ĳ���GPS��λRMC��Ϣ��
	//����յ������ݳ���С��20��˵���յ����������󣬷���0
	if(USART2_RX_BUF[0] != '$' || USART2_RX_BUF[3] != 'R' || (length) < 20)
	{
		return 0;
	}
	
	//�ҳ����ж��ŵ�λ��
	for(i=0; i<(length); i++)
	{
		if(p[i] == 44)//44�Ƕ��ŵ�ASCII��
		{
			comma[j] = i;
			j++;
		}
		//printf("%c", p[i]);
	}

	//���û��λ����Ϣ��ֱ�ӷ���
	if(comma[4] - comma[3] == 1 || comma[6] - comma[5] == 1)//���û��λ����Ϣ��ֱ�ӷ���
	{
		return 0;
	}

	//��3��4����֮�䱣�����γ��
	coordinate = 0;//������
	e = comma[4] - comma[3] - 3;//��ֵ����λ����Ϊʲô�Ǽ�3��һλ�Ƕ��ţ�һλ��С���㣬10��0�η�����1�����Ի�Ҫ�ټ�һ
	for(i=comma[3]+1; i<comma[4]; i++)//comma[3]+1�������ֿ�ʼ�ĵط�
	{
		//����С���������
		if(p[i] == 46)
		{
			i++;
		}
		
		//�Ӹ�λ��ʼ��ÿһλ���ۼ�����
		//ascii����48~57������
		coordinate = (int)(coordinate + (p[i] - 48) * pow(10, e));
		e--;	
	}
	latitude = (double)coordinate / 100000;//�̶���ʽ��С�����5λ
	
	LatitudeDirection = p[comma[4]+1];//���ĸ�����+1��γ�ȵķ���
//	printf("latitude:%f \n", latitude);


	//��5��6����֮�䱣����Ǿ���
	coordinate = 0;//������
	e = comma[6] - comma[5] - 3;//��ֵ����λ����Ϊʲô�Ǽ�3��һλ�Ƕ��ţ�һλ��С���㣬10��0�η�����1�����Ի�Ҫ�ټ�һ
	for(i=comma[5]+1; i<comma[6]; i++)//comma[3]+1�������ֿ�ʼ�ĵط�
	{
		//����С���������
		if(p[i] == 46)
		{
			i++;
		}
		
		//�Ӹ�λ��ʼ��ÿһλ���ۼ�����
		//ascii����48~57������
		coordinate = (int)(coordinate + (p[i] - 48) * pow(10, e));
		e--;	
	}
	longtitude = (double)coordinate / 100000;//�̶���ʽ��С�����5λ
	
	LongtitudeDirection = p[comma[6]+1];//���ĸ�����+1�Ǿ��ȵķ���
//	printf("longtitude:%f \n", longtitude);

	return 1;//��λ�ɹ�����1
}

double ChangeCoord(double x)
{
	//���㹫ʽ
	//���ݸ�ʽΪabcde.fghi
	//���㹫ʽΪabc+(de/60)+(fghi/600000)
	//����һ��������ʧ������
	//����һ��������ʧ������
	//����һ��������ʧ������
	//a = (float)((int)n/100 + (float)((((int)n % 100)/60)) + (float)((n - (int)n)/600000));//����ת��������int�����ݣ������ã�
	double a;

	a = (int)x/100;//���abc
	a += (double)((int)x % 100)/60;//���de/60
	a += (double)(x - (int)x) * 10000 / 600000;//���fghi/600000

	return a;
}
