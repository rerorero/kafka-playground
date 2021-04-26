package main

import (
	"context"
	"fmt"
	"os"

	"github.com/Shopify/sarama"
	"github.com/sethvargo/go-envconfig"
)

type Config struct {
	KafkaVersion  string   `env:"KAFKA_VERSION,default=2.7.0"`
	ConsumerGroup string   `env:"CONSUMER_GROUP,default=default"`
	Brokers       []string `env:"BROKERS"`
	Topics        []string `env:"TOPICS"`
}

type TestConsumer struct {
	consumer sarama.ConsumerGroup
	topics   []string
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

	consumer, err := newConsumer(&conf)
	if err != nil {
		return err
	}
	defer consumer.Close()

	if err := consumer.Run(ctx); err != nil {
		return err
	}

	return nil
}

func newConsumer(c *Config) (*TestConsumer, error) {
	version, err := sarama.ParseKafkaVersion(c.KafkaVersion)
	if err != nil {
		return nil, fmt.Errorf("failed to parse kafka version %s: %w", c.KafkaVersion, err)
	}

	cfg := sarama.NewConfig()
	cfg.Version = version
	cfg.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategySticky
	consumer, err := sarama.NewConsumerGroup(c.Brokers, c.ConsumerGroup, cfg)
	if err != nil {
		return nil, fmt.Errorf("unable to create consumer group: %w", err)
	}

	return &TestConsumer{
		consumer: consumer,
		topics:   c.Topics,
	}, nil
}

func (c *TestConsumer) Run(ctx context.Context) error {
	for {
		if ctx.Err() != nil {
			return nil
		}
		if err := c.consumer.Consume(ctx, c.topics, c); err != nil {
			return fmt.Errorf("unable to consume: %w", err)
		}
		println("rebalanced consumers")
	}
}

// Setup is run at the beginning of a new session, before ConsumeClaim.
func (c *TestConsumer) Setup(sess sarama.ConsumerGroupSession) error {
	println("Setup is called:", sess.MemberID())
	return nil
}

// Cleanup is run at the end of a session, once all ConsumeClaim goroutines have exited
// but before the offsets are committed for the very last time.
func (c *TestConsumer) Cleanup(sarama.ConsumerGroupSession) error {
	println("Cleanup is called")
	return nil
}

// ConsumeClaim must start a consumer loop of ConsumerGroupClaim's Messages().
// Once the Messages() channel is closed, the Handler must finish its processing
// loop and exit.
func (c *TestConsumer) ConsumeClaim(sess sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for m := range claim.Messages() {
		sess.MarkMessage(m, "")
		fmt.Fprintf(os.Stdout, "message consumed successfully topic=%s,partition=%v, offset=%v, key=%s, value=%s\n",
			m.Topic, m.Partition, m.Offset, string(m.Key), string(m.Value))
	}
	return nil
}

func (c *TestConsumer) Close() {
	c.consumer.Close()
}
