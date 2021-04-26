package main

import (
	"context"
	"fmt"
	"os"
	"time"

	"github.com/Shopify/sarama"
	"github.com/sethvargo/go-envconfig"
)

type Config struct {
	Brokers    []string `env:"BROKERS"`
	Topic      string   `env:"TOPIC"`
	IntervalMS int      `env:"INTERVAL_MS,default=1000"`
}

type TestProducer struct {
	producer sarama.SyncProducer
	topic    string
	interval time.Duration
}

func main() {
	ctx := context.Background()
	if err := mainmain(ctx); err != nil {
		fmt.Fprintf(os.Stderr, "[ERROR] %s\n", err)
		os.Exit(1)
	}
	os.Exit(0)
}

func mainmain(ctx context.Context) error {
	var conf Config
	if err := envconfig.Process(ctx, &conf); err != nil {
		return fmt.Errorf("failed to parse Conf: %w", err)
	}

	producer, err := newProducer(&conf)
	if err != nil {
		return err
	}
	defer producer.Close()

	if err := producer.Run(ctx); err != nil {
		return err
	}

	return nil
}

func newProducer(c *Config) (*TestProducer, error) {
	cfg := sarama.NewConfig()
	cfg.Producer.RequiredAcks = sarama.WaitForAll
	cfg.Producer.Return.Successes = true
	sp, err := sarama.NewSyncProducer(c.Brokers, cfg)
	if err != nil {
		return nil, fmt.Errorf("unable to create sync producer: %w", err)
	}

	return &TestProducer{
		producer: sp,
		topic:    c.Topic,
		interval: time.Millisecond * time.Duration(c.IntervalMS),
	}, nil
}

func (p *TestProducer) Run(ctx context.Context) error {
	ticker := time.NewTicker(p.interval)
	for {
		select {
		case <-ctx.Done():
			println("DONE")
			return nil
		case <-ticker.C:
			if err := p.send(ctx); err != nil {
				return err
			}
		}
	}
}

func (p *TestProducer) send(ctx context.Context) error {
	now := time.Now()
	key := fmt.Sprintf("%v", now.Unix())
	value := now.Format(time.RFC3339)
	partition, offset, err := p.producer.SendMessage(&sarama.ProducerMessage{
		Topic: p.topic,
		Key:   sarama.StringEncoder(key),
		Value: sarama.StringEncoder(value),
	})
	if err != nil {
		return fmt.Errorf("unable to send message of key=%s: %w", key, err)
	}

	fmt.Fprintf(os.Stdout, "message sent successfully, key=%s, partition=%v, offset=%v\n", key, partition, offset)
	return nil
}

func (p *TestProducer) Close() {
	p.producer.Close()
}
